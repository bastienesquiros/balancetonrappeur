package org.balancetonrappeur.dto.api;

import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.entity.RapperStatus;

import java.util.List;

/**
 * Résultat du scan Spotify : un rappeur cancel trouvé dans la bibliothèque,
 * avec les titres concernés et ses affaires documentées.
 */
public record ScanResultDto(
        long rapperId,
        String rapperName,
        String rapperImageUrl,
        RapperStatus rapperStatus,
        List<MatchedTrack> tracks,
        List<AccusationSummary> accusations
) {
    /** Construit un ScanResultDto depuis les entités, dans la transaction. */
    public static ScanResultDto from(Rapper rapper, List<MatchedTrack> tracks) {
        var accusations = rapper.getAccusations().stream()
                .map(a -> new AccusationSummary(a.getTitle(), a.getCategory().name()))
                .toList();
        return new ScanResultDto(
                rapper.getId(),
                rapper.getName(),
                rapper.getSpotifyImageUrl(),
                rapper.getStatus(),
                tracks,
                accusations
        );
    }

    public record MatchedTrack(
            String trackName,
            String trackUri,
            String playlistId,
            String playlistName
    ) {}

    public record AccusationSummary(String title, String category) {}
}
