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
            case ACQUITTED -> "Relaxé ou classé sans suite";
            case CONVICTED -> "Condamné";
        };
    }
}
