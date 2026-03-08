package org.balancetonrappeur.spotify.dto;

import java.util.List;

/** Représente un titre Spotify avec ses artistes et son origine (liked / playlist). */
public record SpotifyTrackDto(
        String id,
        String name,
        String uri,
        List<ArtistRef> artists,
        String playlistId,    // null si liked song
        String playlistName   // null si liked song
) {
    public record ArtistRef(String id, String name) {}

    public boolean isLiked() {
        return playlistId == null;
    }
}

