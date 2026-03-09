package org.balancetonrappeur.service;

import org.balancetonrappeur.dto.view.HomeStatsDto;
import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.entity.RapperStatus;
import org.balancetonrappeur.repository.AccusationRepository;
import org.balancetonrappeur.repository.RapperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RapperService {

    private final RapperRepository rapperRepository;
    private final AccusationRepository accusationRepository;

    public HomeStatsDto getHomeStats() {
        var recentRappers = rapperRepository.findAll(
                PageRequest.of(0, 8, Sort.by("createdAt").descending())
        ).getContent();
        return new HomeStatsDto(
                recentRappers,
                rapperRepository.count(),
                rapperRepository.countByStatus(RapperStatus.CONVICTED),
                rapperRepository.countByStatus(RapperStatus.ACCUSED),
                accusationRepository.count()
        );
    }

    public Page<Rapper> findFiltered(RapperStatus status, Pageable pageable) {
        return status != null
                ? rapperRepository.findByStatus(status, pageable)
                : rapperRepository.findAll(pageable);
    }

    public List<Rapper> findAll() {
        return rapperRepository.findAll();
    }

    @Transactional
    public Optional<Rapper> findByIdWithAccusations(Long id) {
        var opt = rapperRepository.findByIdWithAccusations(id);
        opt.ifPresent(rapper ->
                rapper.getAccusations().forEach(a -> {
                    @SuppressWarnings("unused")
                    int ignored = a.getSources().size(); // force eager load — évite MultipleBagFetchException
                })
        );
        return opt;
    }

    public Optional<Accusation> findAccusationOnRapper(Long rapperId, Long accusationId) {
        return findByIdWithAccusations(rapperId)
                .flatMap(r -> r.getAccusations().stream()
                        .filter(a -> a.getId().equals(accusationId))
                        .findFirst());
    }

    public List<Rapper> findSimilar(Long excludeId, RapperStatus status) {
        return rapperRepository.findByStatus(status).stream()
                .filter(r -> !r.getId().equals(excludeId))
                .limit(6)
                .toList();
    }

    public List<Rapper> search(String query) {
        return rapperRepository.findByNameContainingIgnoreCase(query);
    }

    public List<Rapper> searchForAutocomplete(String query) {
        return rapperRepository.findByNameContainingIgnoreCase(query, PageRequest.of(0, 6));
    }

    public Optional<Rapper> findByNameIgnoreCase(String name) {
        return rapperRepository.findByNameIgnoreCase(name);
    }
}
