package org.balancetonrappeur.entity;

public enum RapperStatus {
    ACCUSED,
    ONGOING,
    ACQUITTED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case ACCUSED   -> "Mis en cause";
            case ONGOING   -> "Procédure en cours";
            case ACQUITTED -> "Hors de cause";
            case CONVICTED -> "Condamné";
        };
    }
}
