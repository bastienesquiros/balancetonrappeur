package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.WithdrawalRequest;
import org.balancetonrappeur.entity.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    @Query("SELECT w FROM WithdrawalRequest w WHERE w.status = :status AND w.createdAt >= :since ORDER BY w.createdAt DESC")
    List<WithdrawalRequest> findByStatusSince(@Param("status") WithdrawalStatus status,
                                              @Param("since") LocalDateTime since);

    long countByStatus(WithdrawalStatus status);

    List<WithdrawalRequest> findByStatusOrderByCreatedAtAsc(WithdrawalStatus status);
}

