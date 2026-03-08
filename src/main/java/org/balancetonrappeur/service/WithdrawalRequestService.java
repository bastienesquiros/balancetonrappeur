package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.WithdrawalRequest;
import org.balancetonrappeur.repository.WithdrawalRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawalRequestService {

    private final WithdrawalRequestRepository repository;

    @Transactional
    public void save(WithdrawalRequest request) {
        repository.save(request);
    }
}

