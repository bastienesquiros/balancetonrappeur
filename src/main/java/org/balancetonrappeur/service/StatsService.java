package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.repository.AccusationRepository;
import org.balancetonrappeur.repository.RapperRepository;
import org.balancetonrappeur.repository.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final AccusationRepository accusationRepository;
    private final RapperRepository rapperRepository;
    private final SourceRepository sourceRepository;

    public Stats compute() {

        // Rappeurs
        long totalRappers    = rapperRepository.count();
        long convicted       = rapperRepository.findByStatus(RapperStatus.CONVICTED).size();
        long accused         = rapperRepository.findByStatus(RapperStatus.ACCUSED).size();
        long controversy     = rapperRepository.findByStatus(RapperStatus.CONTROVERSY).size();

        // Accusations
        long totalAccusations = accusationRepository.count();

        // Par catégorie
        Map<String, Long> byCategory = new LinkedHashMap<>();
        for (AccusationCategory cat : AccusationCategory.values()) byCategory.put(cat.name(), 0L);
        for (Object[] row : accusationRepository.countByCategory())
            byCategory.put(((AccusationCategory) row[0]).name(), (Long) row[1]);

        // Par statut juridique
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (AccusationStatus st : AccusationStatus.values()) byStatus.put(st.name(), 0L);
        for (Object[] row : accusationRepository.countByStatus())
            byStatus.put(((AccusationStatus) row[0]).name(), (Long) row[1]);

        // Par année
        Map<String, Long> byYear = new LinkedHashMap<>();
        for (Object[] row : accusationRepository.countByYear())
            byYear.put(String.valueOf(row[0]), (Long) row[1]);

        // Top rappeurs
        Map<String, Long> topRappers = new LinkedHashMap<>();
        for (Object[] row : accusationRepository.topRappersByAccusationCount())
            topRappers.put((String) row[0], (Long) row[1]);

        // Sources par type
        Map<String, Long> bySourceType = new LinkedHashMap<>();
        for (Object[] row : sourceRepository.countByType())
            bySourceType.put(((SourceType) row[0]).name(), (Long) row[1]);

        long totalSources = sourceRepository.count();

        // Max pour les barres de progression — calculé ici, pas dans Thymeleaf
        long maxCat    = byCategory.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxRapper = topRappers.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxYear   = byYear.values().stream().mapToLong(v -> v).max().orElse(1);
        long maxSource = bySourceType.values().stream().mapToLong(v -> v).max().orElse(1);

        return new Stats(
            totalRappers, convicted, accused, controversy,
            totalAccusations, totalSources,
            byCategory, byStatus, byYear, topRappers, bySourceType,
            maxCat, maxRapper, maxYear, maxSource
        );
    }

    public record Stats(
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
}

