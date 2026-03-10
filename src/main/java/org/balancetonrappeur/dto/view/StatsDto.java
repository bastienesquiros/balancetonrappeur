package org.balancetonrappeur.dto.view;

import org.balancetonrappeur.repository.StatsProjections;

import java.util.List;
import java.util.Map;

public record StatsDto(
    long totalRappers,
    long convicted,
    long accused,
    long ongoing,
    long acquitted,
    long totalAccusations,
    long totalSources,
    long accusationsWithoutStatus,
    Map<String, Long> byCategory,
    Map<String, Long> byStatus,
    Map<String, Long> byYear,
    List<StatsProjections.RapperCount> topRappers,
    Map<String, Long> bySourceType,
    long maxCategory,
    long maxRapper,
    long maxYear,
    long maxSource
) {}

