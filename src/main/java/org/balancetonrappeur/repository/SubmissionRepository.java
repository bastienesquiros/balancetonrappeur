package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.Submission;
import org.balancetonrappeur.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    @Query("SELECT s FROM Submission s WHERE s.submissionStatus = :status AND s.createdAt >= :since ORDER BY s.createdAt DESC")
    List<Submission> findByStatusSince(@Param("status") SubmissionStatus status,
                                       @Param("since") LocalDateTime since);

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

