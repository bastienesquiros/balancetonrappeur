package org.balancetonrappeur.entity;

public enum AccusationCategory {
    SEXUAL_ASSAULT,
    RAPE,
    PHYSICAL_VIOLENCE,
    DOMESTIC_VIOLENCE,
    HOMICIDE,
    HATE_SPEECH,
    POLEMIC;

    public String label() {
        return switch (this) {
            case SEXUAL_ASSAULT    -> "Agression sexuelle";
            case RAPE              -> "Viol";
            case PHYSICAL_VIOLENCE -> "Violences physiques";
            case DOMESTIC_VIOLENCE -> "Violences conjugales";
            case HOMICIDE          -> "Homicide";
            case HATE_SPEECH       -> "Incitation à la haine";
            case POLEMIC           -> "Polémique";
        };
    }

    public String color() {
        return switch (this) {
            case SEXUAL_ASSAULT, RAPE, HOMICIDE -> "red";
            case PHYSICAL_VIOLENCE, DOMESTIC_VIOLENCE -> "orange";
            case HATE_SPEECH -> "purple";
            case POLEMIC -> "yellow";
        };
    }
}
