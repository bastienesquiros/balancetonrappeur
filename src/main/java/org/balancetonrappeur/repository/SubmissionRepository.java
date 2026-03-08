package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Submission;
import org.balancetonrappeur.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    long countBySubmissionStatus(SubmissionStatus status);

    @Query("""
        SELECT s FROM Submission s
        LEFT JOIN FETCH s.rapper
        LEFT JOIN FETCH s.sources
        LEFT JOIN FETCH s.accusation
        WHERE s.submissionStatus = :status
        ORDER BY s.createdAt ASC
    """)
    List<Submission> findBySubmissionStatusOrderByCreatedAtAsc(@Param("status") SubmissionStatus status);
}
