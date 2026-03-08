package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Source;
import org.balancetonrappeur.entity.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    List<Source> findByAccusationId(Long accusationId);

    @Query("SELECT s.type, COUNT(s) FROM Source s GROUP BY s.type ORDER BY COUNT(s) DESC")
    List<Object[]> countByType();
}
