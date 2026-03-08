package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.balancetonrappeur.entity.SourceType;

/** Projections JPQL typées pour les requêtes statistiques. */
public final class StatsProjections {

    private StatsProjections() {}

    public record CategoryCount(AccusationCategory category, Long count) {}
    public record StatusCount(AccusationStatus status, Long count) {}
    public record YearCount(Integer year, Long count) {}
    public record RapperCount(String rapperName, Long count) {}
    public record SourceTypeCount(SourceType type, Long count) {}
}

