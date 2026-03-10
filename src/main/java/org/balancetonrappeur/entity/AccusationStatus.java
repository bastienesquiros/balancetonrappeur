package org.balancetonrappeur.entity;

public enum AccusationStatus {
    ONGOING,
    ACQUITTED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case ONGOING   -> "En cours";
            case ACQUITTED -> "Hors de cause";
            case CONVICTED -> "Condamné";
        };
    }
}
