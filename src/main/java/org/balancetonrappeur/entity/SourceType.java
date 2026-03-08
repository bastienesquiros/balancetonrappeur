package org.balancetonrappeur.entity;

public enum SourceType {
    PRESS,
    JUDICIAL,
    SOCIAL_MEDIA,
    OTHER;

    public String label() {
        return switch (this) {
            case PRESS       -> "Presse";
            case JUDICIAL    -> "Décision judiciaire";
            case SOCIAL_MEDIA -> "Réseau social";
            case OTHER       -> "Autre";
        };
    }
}
