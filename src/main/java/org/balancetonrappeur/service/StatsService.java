package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.dto.StatsDto;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.repository.AccusationRepository;
import org.balancetonrappeur.repository.RapperRepository;
import org.balancetonrappeur.repository.SourceRepository;
import org.balancetonrappeur.repository.StatsProjections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final AccusationRepository accusationRepository;
    private final RapperRepository rapperRepository;
    private final SourceRepository sourceRepository;

    public StatsDto compute() {

        long totalRappers = rapperRepository.count();
        long convicted    = rapperRepository.countByStatus(RapperStatus.CONVICTED);
        long accused      = rapperRepository.countByStatus(RapperStatus.ACCUSED);
        long controversy  = rapperRepository.countByStatus(RapperStatus.CONTROVERSY);

        // Accusations
        long totalAccusations = accusationRepository.count();

        // Par catégorie
        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (AccusationCategory cat : AccusationCategory.values()) byCategory.put(cat.name(), 0L);
        for (var row : accusationRepository.countByCategory())
            byCategory.put(row.category().name(), row.count());

        // Par statut juridique
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (AccusationStatus st : AccusationStatus.values()) byStatus.put(st.name(), 0L);
        for (var row : accusationRepository.countByStatus())
            byStatus.put(row.status().name(), row.count());

        // Par année
        Map<String, Long> byYear = new LinkedHashMap<>();
        for (var row : accusationRepository.countByYear())
            byYear.put(String.valueOf(row.year()), row.count());

        // Top rappeurs
        Map<String, Long> topRappers = new LinkedHashMap<>();
        for (var row : accusationRepository.topRappersByAccusationCount())
            topRappers.put(row.rapperName(), row.count());

        // Sources par type
        Map<String, Long> bySourceType = new LinkedHashMap<>();
        for (var row : sourceRepository.countByType())
            bySourceType.put(row.type().name(), row.count());

        long totalSources = sourceRepository.count();

        // Max pour les barres de progression — calculé ici, pas dans Thymeleaf
        long maxCat    = byCategory.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxRapper = topRappers.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxYear   = byYear.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxSource = bySourceType.values().stream().mapToLong(v -> v).max().orElse(1);

        return new StatsDto(
            totalRappers, convicted, accused, controversy,
            totalAccusations, totalSources,
            byCategory, byStatus, byYear, topRappers, bySourceType,
            maxCat, maxRapper, maxYear, maxSource
        );
    }
}

