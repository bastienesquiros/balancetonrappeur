package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.dto.api.ScanResultDto;
import org.balancetonrappeur.exception.SpotifyRateLimitException;
import org.balancetonrappeur.repository.RapperRepository;
import org.balancetonrappeur.spotify.client.SpotifyUserClient;
import org.balancetonrappeur.spotify.dto.SpotifyTrackDto;
import org.balancetonrappeur.util.StringNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {

    private static final int RATE_LIMIT_DELAY_MS = 500;

    private final SpotifyUserClient spotifyUserClient;
    private final RapperRepository rapperRepository;

    public record ScanResult(List<ScanResultDto> results, boolean playlistsUnavailable) {}

    /**
     * Scanne les liked songs + toutes les playlists de l'utilisateur.
     * Retourne les rappeurs indexés dans notre BDD trouvés parmi les artistes.
     */
    @Transactional(readOnly = true)
    public ScanResult scan(String accessToken) {
        log.info("[Scan] Démarrage du scan bibliothèque");

        boolean playlistsUnavailable = false;
        List<SpotifyTrackDto> allTracks = new ArrayList<>(spotifyUserClient.getLikedTracks(accessToken));
        pause();

        try {
            for (var playlist : spotifyUserClient.getPlaylists(accessToken)) {
                pause();
                allTracks.addAll(spotifyUserClient.getPlaylistTracks(accessToken, playlist.id(), playlist.name()));
            }
        } catch (SpotifyRateLimitException e) {
            log.warn("[Scan] Playlists ignorées — quota Spotify atteint. Résultats sur les sons likés uniquement.");
            playlistsUnavailable = true;
        }

        log.info("[Scan] {} titres récupérés", allTracks.size());

        var byArtist = buildArtistIndex(allTracks);
        var results = matchRappers(byArtist);

        log.info("[Scan] {} rappeur(s) cancel trouvé(s)", results.size());
        return new ScanResult(results, playlistsUnavailable);
    }

    private Map<String, List<SpotifyTrackDto>> buildArtistIndex(List<SpotifyTrackDto> tracks) {
        Map<String, List<SpotifyTrackDto>> index = new HashMap<>();
        for (var track : tracks) {
            if (track.artists() == null) continue;
            for (var artist : track.artists()) {
                index.computeIfAbsent(StringNormalizer.normalize(artist.name()), k -> new ArrayList<>())
                        .add(track);
            }
        }
        return index;
    }

    private List<ScanResultDto> matchRappers(Map<String, List<SpotifyTrackDto>> byArtist) {
        var results = new ArrayList<ScanResultDto>();
        for (var rapper : rapperRepository.findAll()) {
            var matched = byArtist.getOrDefault(StringNormalizer.normalize(rapper.getName()), List.of());
            if (matched.isEmpty()) continue;
            var tracks = deduplicate(matched);
            results.add(ScanResultDto.from(rapper, tracks));
            log.info("[Scan] Rappeur cancel trouvé : {} ({} titre(s))", rapper.getName(), tracks.size());
        }
        return results;
    }

    private List<ScanResultDto.MatchedTrack> deduplicate(List<SpotifyTrackDto> tracks) {
        var seen = new LinkedHashMap<String, ScanResultDto.MatchedTrack>();
        for (var track : tracks) {
            String key = track.uri() + "|" + (track.playlistId() != null ? track.playlistId() : "liked");
            seen.put(key, new ScanResultDto.MatchedTrack(
                    track.name(), track.uri(), track.playlistId(), track.playlistName()
            ));
        }
        return new ArrayList<>(seen.values());
    }

    private void pause() {
        try {
            Thread.sleep(RATE_LIMIT_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
