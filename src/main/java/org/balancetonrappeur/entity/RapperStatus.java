package org.balancetonrappeur.entity;

public enum RapperStatus {
    CONTROVERSY,
    ACCUSED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case CONVICTED  -> "⛔ Condamné";
            case ACCUSED    -> "🚨 Accusé";
            case CONTROVERSY -> "⚠️ Polémique";
        };
    }
}
