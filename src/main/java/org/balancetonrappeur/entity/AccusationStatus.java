package org.balancetonrappeur.entity;

public enum AccusationStatus {
    ONGOING,
    ACQUITTED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case ONGOING   -> "En cours";
            case ACQUITTED -> "Acquitté";
            case CONVICTED -> "Condamné";
        };
    }
}
