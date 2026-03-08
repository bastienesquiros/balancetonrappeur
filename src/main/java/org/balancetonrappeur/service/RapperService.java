package org.balancetonrappeur.service;

import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.entity.RapperStatus;
import org.balancetonrappeur.repository.RapperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapperService {

    private final RapperRepository rapperRepository;

    public List<Rapper> findAll() { return rapperRepository.findAll(); }
    public Page<Rapper> findAll(Pageable pageable) { return rapperRepository.findAll(pageable); }

    public Optional<Rapper> findById(Long id) { return rapperRepository.findById(id); }
    @Transactional
    public Optional<Rapper> findByIdWithAccusations(Long id) {
        // 1ère passe : charge le rapper + accusations (sans sources → pas de doublon)
        var opt = rapperRepository.findByIdWithAccusations(id);
        // 2ème passe : force le chargement des sources pour chaque accusation
        opt.ifPresent(rapper ->
            rapper.getAccusations().forEach(a -> a.getSources().size())
        );
        return opt;
    }

    public List<Rapper> findByStatus(RapperStatus status) { return rapperRepository.findByStatus(status); }
    public Page<Rapper> findByStatus(RapperStatus status, Pageable pageable) { return rapperRepository.findByStatus(status, pageable); }

    public List<Rapper> search(String query) { return rapperRepository.findByNameContainingIgnoreCase(query); }
    public List<Rapper> searchForAutocomplete(String query) { return rapperRepository.findByNameContainingIgnoreCase(query, PageRequest.of(0, 6)); }
    public Optional<Rapper> findByNameIgnoreCase(String name) { return rapperRepository.findByNameIgnoreCase(name); }
}
