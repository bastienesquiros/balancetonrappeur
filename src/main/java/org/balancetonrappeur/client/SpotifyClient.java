package org.balancetonrappeur.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.dto.SpotifyArtistDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.text.Normalizer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class SpotifyClient {

    @Value("${spotify.client-id:}")
    private String clientId;

    @Value("${spotify.client-secret:}")
    private String clientSecret;

    private final RestClient restClient = RestClient.create();

    // ── DTOs internes ─────────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    record TokenResponse(@JsonProperty("access_token") String accessToken) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ArtistRaw {
        @JsonProperty("id")     String id;
        @JsonProperty("name")   String name;
        @JsonProperty("images") List<ImageRaw> images;

        SpotifyArtistDto toDto() {
            String imageUrl = (images != null && !images.isEmpty()) ? images.get(0).url : null;
            return new SpotifyArtistDto(id, imageUrl);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ImageRaw {
        @JsonProperty("url") String url;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    static class SearchResponse {
        @JsonProperty("artists") ArtistsPage artists;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ArtistsPage {
        @JsonProperty("items") List<ArtistRaw> items;
    }

    // ── API publique ───────────────────────────────────────────────────────

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public Optional<String> fetchToken() {
        try {
            var credentials = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());
            var body = new LinkedMultiValueMap<String, String>();
            body.add("grant_type", "client_credentials");

            var response = restClient.post()
                    .uri("https://accounts.spotify.com/api/token")
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


    /** Recherche par nom — match exact normalisé obligatoire, sinon rien. */
    public Optional<SpotifyArtistDto> searchArtist(String token, String name) {
        try {
            var response = restClient.get()
                    .uri("https://api.spotify.com/v1/search?q={q}&type=artist&limit=5", name)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(SearchResponse.class);

            if (response == null || response.artists == null || response.artists.items == null || response.artists.items.isEmpty())
                return Optional.empty();

            String normalizedName = normalize(name);
            return response.artists.items.stream()
                    .filter(a -> normalize(a.name).equals(normalizedName))
                    .findFirst()
                    .map(ArtistRaw::toDto)
                    .or(() -> {
                        log.warn("[Spotify] Aucun match exact pour '{}' — sync manuel requis", name);
                        return Optional.empty();
                    });
        } catch (Exception e) {
            log.warn("[Spotify] Erreur searchArtist({}) : {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private String normalize(String input) {
        if (input == null) return "";
        String decomposed = Normalizer.normalize(input, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toLowerCase()
                .replaceAll("[\\-_.']", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
