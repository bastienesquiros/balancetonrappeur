package org.balancetonrappeur.util;

import java.text.Normalizer;

public final class StringNormalizer {

    private StringNormalizer() {}

    /** Normalise un nom d'artiste pour comparaison insensible aux accents, casse et ponctuations. */
    public static String normalize(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}", "")
                .toLowerCase()
                .replaceAll("[\\-_.']", "")
                .replaceAll("\\s+", " ")
                .trim();
    }
}

