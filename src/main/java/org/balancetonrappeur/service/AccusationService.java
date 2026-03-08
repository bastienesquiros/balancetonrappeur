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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccusationService {

    private final AccusationRepository accusationRepository;

    public List<Accusation> findAll() {
        return accusationRepository.findAllWithRapper();
    }

    public Page<Accusation> findAll(Pageable pageable) {
        return accusationRepository.findAllWithRapper(pageable);
    }

    public Optional<Accusation> findById(Long id) {
        return accusationRepository.findByIdWithSources(id);
    }

    public List<Accusation> findByRapper(Long rapperId) {
        return accusationRepository.findByRapperId(rapperId);
    }

    public List<Accusation> findByCategory(AccusationCategory category) {
        return accusationRepository.findByCategoryWithRapper(category);
    }

    public Page<Accusation> findByCategory(AccusationCategory category, Pageable pageable) {
        return accusationRepository.findByCategoryWithRapper(category, pageable);
    }

    public List<Accusation> findByStatus(AccusationStatus status) {
        return accusationRepository.findByStatusWithRapper(status);
    }

    public Page<Accusation> findByStatus(AccusationStatus status, Pageable pageable) {
        return accusationRepository.findByStatusWithRapper(status, pageable);
    }
}
