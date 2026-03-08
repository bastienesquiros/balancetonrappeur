package org.balancetonrappeur.exception;

/** Levée quand Spotify répond 429 — quota journalier atteint. */
public class SpotifyRateLimitException extends RuntimeException {
    public SpotifyRateLimitException() {
        super("Spotify rate limit atteint — réessaie demain.");
    }
}

