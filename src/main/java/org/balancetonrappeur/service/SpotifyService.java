package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.spotify.client.SpotifyClient;
import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.repository.RapperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpotifyService {

    private final SpotifyClient spotifyClient;
    private final RapperRepository rapperRepository;

    @Transactional
    public void syncSpotify(Rapper rapper) {
        spotifyClient.searchArtist(rapper.getName()).ifPresent(dto -> {
            rapper.setSpotifyImageUrl(dto.imageUrl());
            rapperRepository.save(rapper);
            log.info("[Spotify] Photo récupérée pour {}", rapper.getName());
        });
    }
}
