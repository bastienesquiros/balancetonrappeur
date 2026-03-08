package org.balancetonrappeur.entity;

public enum AccusationCategory {
    SEXUAL_ASSAULT,
    PEDOPHILIA,
    PHYSICAL_VIOLENCE,
    CONTROVERSY;

    public String label() {
        return switch (this) {
            case SEXUAL_ASSAULT   -> "Agression sexuelle";
            case PEDOPHILIA       -> "Pédophilie";
            case PHYSICAL_VIOLENCE -> "Violence physique";
            case CONTROVERSY      -> "Polémique";
        };
    }
}
