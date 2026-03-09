package org.balancetonrappeur.dto.view;

import org.balancetonrappeur.entity.Rapper;

import java.util.List;

public record HomeStatsDto(
    List<Rapper> recentRappers,
    long rapperCount,
    long convictedCount,
    long accusedCount,
    long accusationCount
) {}

