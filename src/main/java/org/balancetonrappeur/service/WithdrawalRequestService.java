package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.WithdrawalReason;
import org.balancetonrappeur.entity.WithdrawalRequest;
import org.balancetonrappeur.repository.WithdrawalRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawalRequestService {

    private final WithdrawalRequestRepository repository;

    @Transactional
    public void submit(Long accusationId, String accusationTitle,
                       String rapperName, WithdrawalReason reason,
                       String message, String email) {
        var request = new WithdrawalRequest();
        request.setAccusationId(accusationId);
        request.setAccusationTitle(accusationTitle);
        request.setRapperName(rapperName);
        request.setReason(reason);
        request.setMessage(message);
        request.setEmail(email != null && !email.isBlank() ? email : null);
        repository.save(request);
    }
}
