package org.balancetonrappeur.spotify.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.exception.SpotifyRateLimitException;
import org.balancetonrappeur.spotify.dto.SpotifyPlaylistDto;
import org.balancetonrappeur.spotify.dto.SpotifyTrackDto;
import org.balancetonrappeur.spotify.dto.SpotifyUserTokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Client Spotify OAuth2 Authorization Code.
 * Gère les liked songs, les playlists et la suppression de titres.
 */
@Slf4j
@Component
public class SpotifyUserClient {

    private static final String SPOTIFY_API       = "https://api.spotify.com/v1";
    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_AUTH_URL  = "https://accounts.spotify.com/authorize";
    private static final int RATE_LIMIT_DELAY_MS = 500;

    private static final List<String> SCOPES = List.of(
            "user-library-read",
            "user-library-modify",
            "playlist-read-private",
            "playlist-read-collaborative",
            "playlist-modify-public",
            "playlist-modify-private"
    );

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final RestClient restClient;

    public SpotifyUserClient(
            @Value("${spotify.client-id:}")       String clientId,
            @Value("${spotify.client-secret:}")   String clientSecret,
            @Value("${spotify.redirect-uri:}")    String redirectUri,
            RestClient.Builder builder) {
        this.clientId    = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri  = redirectUri;
        this.restClient   = builder.build();
    }

    // Internal deserialization records

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(
            @JsonProperty("access_token")  String accessToken,
            @JsonProperty("refresh_token") String refreshToken
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArtistRef(
            @JsonProperty("id")   String id,
            @JsonProperty("name") String name
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TrackObject(
            @JsonProperty("id")      String id,
            @JsonProperty("name")    String name,
            @JsonProperty("artists") List<ArtistRef> artists,
            @JsonProperty("uri")     String uri
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SavedTrackItem(@JsonProperty("track") TrackObject track) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SavedTracksPage(
            @JsonProperty("items") List<SavedTrackItem> items,
            @JsonProperty("next")  String next,
            @JsonProperty("total") int total
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Owner(@JsonProperty("id") String id) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PlaylistItem(
            @JsonProperty("id")    String id,
            @JsonProperty("name")  String name,
            @JsonProperty("owner") Owner owner
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PlaylistsPage(
            @JsonProperty("items") List<PlaylistItem> items,
            @JsonProperty("next")  String next
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PlaylistTrackItem(
            @JsonProperty("track") TrackObject track,
            @JsonProperty("item")  TrackObject item
    ) {
        TrackObject resolved() { return item != null ? item : track; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PlaylistTracksPage(
            @JsonProperty("items") List<PlaylistTrackItem> items,
            @JsonProperty("next")  String next
    ) {}

    // Public API

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    /** URL vers laquelle rediriger l'utilisateur pour autorisation. */
    public String buildAuthorizationUrl(String state) {
        return UriComponentsBuilder.fromUriString(SPOTIFY_AUTH_URL)
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", String.join(" ", SCOPES))
                .queryParam("state", state)
                .toUriString();
    }

    /** Échange un code contre un access token. */
    public Optional<SpotifyUserTokenDto> exchangeCode(String code) {
        try {
            var credentials = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());
            var body = new LinkedMultiValueMap<String, String>();
            body.add("grant_type", "authorization_code");
            body.add("code", code);
            body.add("redirect_uri", redirectUri);

            var response = restClient.post()
                    .uri(SPOTIFY_TOKEN_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(TokenResponse.class);

            if (response == null || response.accessToken() == null) return Optional.empty();
            return Optional.of(new SpotifyUserTokenDto(response.accessToken(), response.refreshToken()));
        } catch (Exception e) {
            log.warn("[SpotifyUser] Erreur échange code : {}", e.getMessage());
            return Optional.empty();
        }
    }

    /** Récupère toutes les liked songs. */
    public List<SpotifyTrackDto> getLikedTracks(String accessToken) {
        List<SpotifyTrackDto> all = new ArrayList<>();
        String url = SPOTIFY_API + "/me/tracks?limit=50";
        while (url != null) {
            var page = spotifyGet(url, accessToken, SavedTracksPage.class);
            if (page == null || page.items() == null) break;
            for (var item : page.items()) {
                if (item.track() != null) all.add(toTrackDto(item.track(), null, null));
            }
            url = page.next();
        }
        return all;
    }

    /** Récupère toutes les playlists dont l'utilisateur est owner. */
    public List<SpotifyPlaylistDto> getPlaylists(String accessToken) {
        List<SpotifyPlaylistDto> all = new ArrayList<>();
        String url = SPOTIFY_API + "/me/playlists?limit=50";
        String resolvedUserId = null;
        while (url != null) {
            var page = spotifyGet(url, accessToken, PlaylistsPage.class);
            if (page == null || page.items() == null) break;
            for (var item : page.items()) {
                if (item == null || item.owner() == null) continue;
                if (resolvedUserId == null) resolvedUserId = item.owner().id();
                if (!resolvedUserId.equals(item.owner().id())) continue;
                all.add(new SpotifyPlaylistDto(item.id(), item.name()));
            }
            url = page.next();
        }
        log.info("[SpotifyUser] {} playlist(s) propres trouvées", all.size());
        return all;
    }

    /** Récupère tous les titres d'une playlist. */
    public List<SpotifyTrackDto> getPlaylistTracks(String accessToken, String playlistId, String playlistName) {
        List<SpotifyTrackDto> all = new ArrayList<>();
        String url = SPOTIFY_API + "/playlists/" + playlistId + "/items?limit=100";
        while (url != null) {
            var page = spotifyGet(url, accessToken, PlaylistTracksPage.class);
            if (page == null || page.items() == null) break;
            for (var item : page.items()) {
                var track = item.resolved();
                if (track != null) all.add(toTrackDto(track, playlistId, playlistName));
            }
            url = page.next();
        }
        return all;
    }

    /**
     * Exécute un GET Spotify.
     * Lève {@link SpotifyRateLimitException} sur 429 — l'appelant doit gérer gracieusement.
     */
    private <T> T spotifyGet(String url, String accessToken, Class<T> type) {
        pause();
        try {
            return restClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(type);
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new SpotifyRateLimitException();
        } catch (Exception e) {
            log.warn("[SpotifyUser] Erreur GET {} : {}", url, e.getMessage());
            return null;
        }
    }

    /** Retire un titre des liked songs via son URI. Retourne true si succès. */
    public boolean removeLikedTrack(String accessToken, String trackUri) {
        try {
            var uri = UriComponentsBuilder
                    .fromUriString(SPOTIFY_API + "/me/library")
                    .queryParam("uris", trackUri)
                    .build()
                    .encode()
                    .toUri();
            restClient.method(HttpMethod.DELETE)
                    .uri(uri)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .toBodilessEntity();
            log.info("[SpotifyUser] Liked track retiré : {}", trackUri);
            return true;
        } catch (Exception e) {
            log.warn("[SpotifyUser] Erreur retrait liked track {} : {}", trackUri, e.getMessage());
            return false;
        }
    }

    /** Retire un titre d'une playlist. Retourne true si succès. */
    public boolean removePlaylistTrack(String accessToken, String playlistId, String trackUri) {
        try {
            var body = Map.of("items", List.of(Map.of("uri", trackUri)));
            restClient.method(HttpMethod.DELETE)
                    .uri(SPOTIFY_API + "/playlists/" + playlistId + "/items")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("[SpotifyUser] Track retiré de playlist {} : {}", playlistId, trackUri);
            return true;
        } catch (Exception e) {
            log.warn("[SpotifyUser] Erreur retrait playlist track {} : {}", trackUri, e.getMessage());
            return false;
        }
    }

    private SpotifyTrackDto toTrackDto(TrackObject track, String playlistId, String playlistName) {
        List<SpotifyTrackDto.ArtistRef> artists = track.artists() == null ? List.of() :
                track.artists().stream()
                        .map(a -> new SpotifyTrackDto.ArtistRef(a.id(), a.name()))
                        .toList();
        return new SpotifyTrackDto(track.id(), track.name(), track.uri(), artists, playlistId, playlistName);
    }

    private void pause() {
        sleep(RATE_LIMIT_DELAY_MS);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
