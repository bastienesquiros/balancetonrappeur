package org.balancetonrappeur.spotify.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.spotify.dto.SpotifyArtistDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.balancetonrappeur.util.StringNormalizer;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SpotifyClient {

    private static final String SPOTIFY_API       = "https://api.spotify.com/v1";
    private static final String SPOTIFY_TOKEN_URL = "https://accounts.spotify.com/api/token";

    private final String clientId;
    private final String clientSecret;
    private final RestClient restClient;

    public SpotifyClient(
            @Value("${spotify.client-id:}")     String clientId,
            @Value("${spotify.client-secret:}") String clientSecret,
            RestClient.Builder builder) {
        this.clientId     = clientId;
        this.clientSecret = clientSecret;
        this.restClient   = builder.build();
    }

    // Internal deserialization records (Spotify API contract — private)

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ImageRaw(
            @JsonProperty("url")    String url,
            @JsonProperty("width")  Integer width
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArtistRaw(
            @JsonProperty("id")     String id,
            @JsonProperty("name")   String name,
            @JsonProperty("images") List<ImageRaw> images
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArtistsPage(@JsonProperty("items") List<ArtistRaw> items) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SearchResponse(@JsonProperty("artists") ArtistsPage artists) {}

    // Public API

    /**
     * Recherche un artiste par nom exact (normalisé).
     * Retourne empty si Spotify n'est pas configuré, si aucun match exact, ou en cas d'erreur.
     */
    public Optional<SpotifyArtistDto> searchArtist(String name) {
        if (!isConfigured()) return Optional.empty();
        return fetchToken().flatMap(token -> doSearch(token, name));
    }

    // Private

    private boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    private Optional<String> fetchToken() {
        try {
            var credentials = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());
            var body = new LinkedMultiValueMap<String, String>();
            body.add("grant_type", "client_credentials");

            var response = restClient.post()
                    .uri(SPOTIFY_TOKEN_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(TokenResponse.class);

            return Optional.ofNullable(response).map(TokenResponse::accessToken);
        } catch (Exception e) {
            log.warn("[Spotify] Erreur token : {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<SpotifyArtistDto> doSearch(String token, String name) {
        try {
            var response = restClient.get()
                    .uri(SPOTIFY_API + "/search?q={q}&type=artist&limit=5", name)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(SearchResponse.class);

            if (response == null || response.artists() == null
                    || response.artists().items() == null
                    || response.artists().items().isEmpty()) {
                return Optional.empty();
            }

            String normalizedName = StringNormalizer.normalize(name);
            var match = response.artists().items().stream()
                    .filter(a -> StringNormalizer.normalize(a.name()).equals(normalizedName))
                    .findFirst();

            if (match.isEmpty()) {
                log.warn("[Spotify] Aucun match exact pour '{}' — sync manuel requis", name);
            }

            return match.map(a -> {
                // Pick the smallest image >= 120px (typically 300px) — avoids downloading 640px for ~120px display
                String imageUrl = null;
                if (a.images() != null && !a.images().isEmpty()) {
                    imageUrl = a.images().stream()
                            .filter(img -> img.width() != null && img.width() >= 120)
                            .min((i1, i2) -> Integer.compare(i1.width(), i2.width()))
                            .map(ImageRaw::url)
                            .orElse(a.images().getFirst().url()); // fallback to largest
                }
                return new SpotifyArtistDto(a.id(), imageUrl);
            });
        } catch (Exception e) {
            log.warn("[Spotify] Erreur searchArtist({}) : {}", name, e.getMessage());
            return Optional.empty();
        }
    }
}
