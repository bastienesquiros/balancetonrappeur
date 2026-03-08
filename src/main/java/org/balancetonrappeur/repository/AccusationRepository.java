package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccusationRepository extends JpaRepository<Accusation, Long> {

    List<Accusation> findByRapperId(Long rapperId);
    List<Accusation> findByCategory(AccusationCategory category);
    List<Accusation> findByStatus(AccusationStatus status);

    @Query("""
        SELECT a FROM Accusation a
        LEFT JOIN FETCH a.sources
        LEFT JOIN FETCH a.rapper
        WHERE a.id = :id
    """)
    Optional<Accusation> findByIdWithSources(@Param("id") Long id);

    // Timeline — seulement les accusations avec une date, triées DESC
    @Query("SELECT a FROM Accusation a JOIN FETCH a.rapper WHERE a.factDate IS NOT NULL ORDER BY a.factDate DESC")
    List<Accusation> findAllForTimeline();

    @Query("SELECT a FROM Accusation a JOIN FETCH a.rapper ORDER BY a.factDate DESC NULLS LAST")
    List<Accusation> findAllWithRapper();

    @Query(value = "SELECT a FROM Accusation a JOIN FETCH a.rapper ORDER BY a.factDate DESC NULLS LAST",
           countQuery = "SELECT COUNT(a) FROM Accusation a")
    Page<Accusation> findAllWithRapper(Pageable pageable);

    @Query("SELECT a FROM Accusation a JOIN FETCH a.rapper WHERE a.category = :category ORDER BY a.factDate DESC NULLS LAST")
    List<Accusation> findByCategoryWithRapper(@Param("category") AccusationCategory category);

    @Query(value = "SELECT a FROM Accusation a JOIN FETCH a.rapper WHERE a.category = :category ORDER BY a.factDate DESC NULLS LAST",
           countQuery = "SELECT COUNT(a) FROM Accusation a WHERE a.category = :category")
    Page<Accusation> findByCategoryWithRapper(@Param("category") AccusationCategory category, Pageable pageable);

    @Query("SELECT a FROM Accusation a JOIN FETCH a.rapper WHERE a.status = :status ORDER BY a.factDate DESC NULLS LAST")
    List<Accusation> findByStatusWithRapper(@Param("status") AccusationStatus status);

    @Query(value = "SELECT a FROM Accusation a JOIN FETCH a.rapper WHERE a.status = :status ORDER BY a.factDate DESC NULLS LAST",
           countQuery = "SELECT COUNT(a) FROM Accusation a WHERE a.status = :status")
    Page<Accusation> findByStatusWithRapper(@Param("status") AccusationStatus status, Pageable pageable);

    // ── Stats ──────────────────────────────────────────────────────────────

    // Nb d'affaires par catégorie : [category, count]
    @Query("SELECT a.category, COUNT(a) FROM Accusation a GROUP BY a.category ORDER BY COUNT(a) DESC")
    List<Object[]> countByCategory();

    // Nb d'affaires par statut juridique : [status, count]
    @Query("SELECT a.status, COUNT(a) FROM Accusation a GROUP BY a.status ORDER BY COUNT(a) DESC")
    List<Object[]> countByStatus();

    // Nb d'affaires par année — EXTRACT compatible PostgreSQL
    @Query("SELECT CAST(EXTRACT(YEAR FROM a.factDate) AS integer), COUNT(a) FROM Accusation a WHERE a.factDate IS NOT NULL GROUP BY EXTRACT(YEAR FROM a.factDate) ORDER BY EXTRACT(YEAR FROM a.factDate)")
    List<Object[]> countByYear();

    // Top rappeurs par nb d'affaires
    @Query("SELECT a.rapper.name, COUNT(a) FROM Accusation a GROUP BY a.rapper.name ORDER BY COUNT(a) DESC")
    List<Object[]> topRappersByAccusationCount();
}
