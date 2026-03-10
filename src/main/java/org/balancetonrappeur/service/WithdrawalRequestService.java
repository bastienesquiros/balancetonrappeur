package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.entity.WithdrawalReason;
import org.balancetonrappeur.entity.WithdrawalRequest;
import org.balancetonrappeur.repository.AccusationRepository;
import org.balancetonrappeur.repository.WithdrawalRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WithdrawalRequestService {

    private final WithdrawalRequestRepository repository;
    private final AccusationRepository accusationRepository;

    @Transactional
    public void submit(Long accusationId, String accusationTitle,
                       String rapperName, WithdrawalReason reason,
                       String message, String email) {
        var request = new WithdrawalRequest();

        if (accusationId != null) {
            Accusation accusation = accusationRepository.findById(accusationId).orElse(null);
            request.setAccusation(accusation);
        }

        request.setAccusationTitle(accusationTitle);
        request.setRapperName(rapperName);
        request.setReason(reason);
        request.setMessage(message);
        request.setEmail(email != null && !email.isBlank() ? email : null);
        repository.save(request);
    }
}


