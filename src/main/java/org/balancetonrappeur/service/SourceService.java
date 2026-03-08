package org.balancetonrappeur.service;

import org.balancetonrappeur.entity.Source;
import org.balancetonrappeur.repository.SourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SourceService {

    private final SourceRepository sourceRepository;

    public Optional<Source> findById(Long id) {
        return sourceRepository.findById(id);
    }

    public List<Source> findByAccusation(Long accusationId) {
        return sourceRepository.findByAccusationId(accusationId);
    }
}

