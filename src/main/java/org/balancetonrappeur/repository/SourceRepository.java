package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {

    @Query("SELECT new org.balancetonrappeur.repository.StatsProjections$SourceTypeCount(s.type, COUNT(s)) FROM Source s GROUP BY s.type ORDER BY COUNT(s) DESC")
    List<StatsProjections.SourceTypeCount> countByType();
}
