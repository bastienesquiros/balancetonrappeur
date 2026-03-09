package org.balancetonrappeur.service;

import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.balancetonrappeur.repository.AccusationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccusationService {

    private final AccusationRepository accusationRepository;


    public Page<Accusation> findFiltered(AccusationCategory category, AccusationStatus status, Pageable pageable) {
        if (category != null) return accusationRepository.findByCategoryInWithRapper(List.of(category), pageable);
        if (status   != null) return accusationRepository.findByStatusInWithRapper(List.of(status), pageable);
        return accusationRepository.findAllWithRapper(pageable);
    }


    public Optional<Accusation> findById(Long id) {
        return accusationRepository.findByIdWithSources(id);
    }

    public List<Accusation> findByRapper(Long rapperId) {
        return accusationRepository.findByRapperId(rapperId);
    }

    public Map<Integer, List<Accusation>> findGroupedByYear() {
        Map<Integer, List<Accusation>> byYear = new LinkedHashMap<>();
        for (Accusation a : accusationRepository.findAllForTimeline()) {
            byYear.computeIfAbsent(a.getFactDate().getYear(), k -> new ArrayList<>()).add(a);
        }
        return byYear;
    }


}
