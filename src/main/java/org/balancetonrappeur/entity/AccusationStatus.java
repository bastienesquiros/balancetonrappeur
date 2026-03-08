package org.balancetonrappeur.entity;

public enum AccusationStatus {
    CONTROVERSY,
    ONGOING,
    ACQUITTED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case CONTROVERSY -> "⚠️ Polémique";
            case ONGOING     -> "🚨 En cours";
            case ACQUITTED   -> "✅ Acquitté";
            case CONVICTED   -> "⛔ Condamné";
        };
    }
}
