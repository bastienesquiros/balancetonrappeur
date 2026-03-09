package org.balancetonrappeur.entity;

public enum AccusationStatus {
    POLEMIC,
    ONGOING,
    ACQUITTED,
    CONVICTED;

    public String label() {
        return switch (this) {
            case POLEMIC    -> "Polémique";
            case ONGOING    -> "En cours";
            case ACQUITTED  -> "Acquitté";
            case CONVICTED  -> "Condamné";
        };
    }
}
