package org.balancetonrappeur.exception;

public class NoChangeDetectedException extends RuntimeException {
    public NoChangeDetectedException() {
        super("Aucune modification détectée par rapport à l'affaire existante.");
    }
}

