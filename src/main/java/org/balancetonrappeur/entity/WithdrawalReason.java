package org.balancetonrappeur.entity;

public enum WithdrawalReason {
    INCORRECT_INFO,
    OUTDATED_INFO,
    PRIVACY,
    OTHER;

    public String label() {
        return switch (this) {
            case INCORRECT_INFO -> "Information incorrecte";
            case OUTDATED_INFO  -> "Information obsolète";
            case PRIVACY        -> "Atteinte à la vie privée";
            case OTHER          -> "Autre";
        };
    }
}

