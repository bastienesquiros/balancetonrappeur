package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Rapper;
import org.balancetonrappeur.entity.RapperStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RapperRepository extends JpaRepository<Rapper, Long> {

    List<Rapper> findByStatus(RapperStatus status);
    Page<Rapper> findByStatus(RapperStatus status, Pageable pageable);

    // Recherche pour la page /search (résultats complets)
    Optional<Rapper> findByNameIgnoreCase(String name);

    List<Rapper> findByNameContainingIgnoreCase(String name);

    // Recherche pour l'autocomplete — limité à N résultats côté SQL
    List<Rapper> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
        SELECT DISTINCT r FROM Rapper r
        LEFT JOIN FETCH r.accusations
        WHERE r.id = :id
    """)
    Optional<Rapper> findByIdWithAccusations(@Param("id") Long id);

    long countBySpotifyImageUrlIsNull();
    List<Rapper> findBySpotifyImageUrlIsNull();
}

