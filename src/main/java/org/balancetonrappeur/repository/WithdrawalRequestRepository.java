package org.balancetonrappeur.repository;

import org.balancetonrappeur.entity.WithdrawalRequest;
import org.balancetonrappeur.entity.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRequestRepository extends JpaRepository<WithdrawalRequest, Long> {

    long countByStatus(WithdrawalStatus status);

    List<WithdrawalRequest> findByStatusOrderByCreatedAtAsc(WithdrawalStatus status);
}
