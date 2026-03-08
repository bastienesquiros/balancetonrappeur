package org.balancetonrappeur.dto;

import java.util.Map;

public record StatsDto(
    long totalRappers,
    long convicted,
    long accused,
    long controversy,
    long totalAccusations,
    long totalSources,
    Map<String, Long> byCategory,
    Map<String, Long> byStatus,
    Map<String, Long> byYear,
    Map<String, Long> topRappers,
    Map<String, Long> bySourceType,
    long maxCategory,
    long maxRapper,
    long maxYear,
    long maxSource
) {}

