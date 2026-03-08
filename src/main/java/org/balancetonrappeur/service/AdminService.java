package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SubmissionRepository submissionRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final RapperRepository rapperRepository;
    private final AccusationRepository accusationRepository;

    // ── Submissions ───────────────────────────────────────────────────────

    @Transactional
    public Rapper acceptSubmission(Long submissionId) {
        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission #" + submissionId + " introuvable"));

        return switch (submission.getType()) {
            case ADD_ACCUSATION  -> acceptAddAccusation(submission);
            case EDIT_ACCUSATION -> acceptEditAccusation(submission);
        };
    }

    private Rapper acceptAddAccusation(Submission submission) {
        Rapper rapper;

        if (submission.getRapper() != null) {
            rapper = submission.getRapper();
        } else {
            rapper = new Rapper();
            rapper.setName(submission.getUnknownRapperName());
            rapper.setStatus(deriveRapperStatus(submission.getStatus()));
            rapperRepository.save(rapper);
            log.info("[Admin] Rappeur créé : {} ({})", rapper.getName(), rapper.getStatus());
        }

        var accusation = new Accusation();
        accusation.setRapper(rapper);
        accusation.setCategory(submission.getCategory());
        accusation.setTitle(submission.getTitle());
        accusation.setStatus(submission.getStatus());
        accusation.setFactDate(submission.getFactDate());
        accusationRepository.save(accusation);

        for (var ss : submission.getSources()) {
            var source = new Source();
            source.setAccusation(accusation);
            source.setType(ss.getType());
            source.setTitle(ss.getTitle());
            source.setUrl(ss.getUrl());
            accusation.getSources().add(source);
        }
        accusationRepository.save(accusation);

        recalculateRapperStatus(rapper);

        submission.setSubmissionStatus(SubmissionStatus.APPROVED);
        submissionRepository.save(submission);
        log.info("[Admin] Submission #{} acceptée — accusation #{} créée", submission.getId(), accusation.getId());
        return rapper;
    }

    private Rapper acceptEditAccusation(Submission submission) {
        var accusation = submission.getAccusation();
        if (accusation == null)
            throw new IllegalStateException("Submission EDIT sans accusation liée");

        accusation.setCategory(submission.getCategory());
        accusation.setTitle(submission.getTitle());
        accusation.setStatus(submission.getStatus());
        accusation.setFactDate(submission.getFactDate());

        // Remplacer les sources
        accusation.getSources().clear();
        for (var ss : submission.getSources()) {
            var source = new Source();
            source.setAccusation(accusation);
            source.setType(ss.getType());
            source.setTitle(ss.getTitle());
            source.setUrl(ss.getUrl());
            accusation.getSources().add(source);
        }
        accusationRepository.save(accusation);

        recalculateRapperStatus(accusation.getRapper());

        submission.setSubmissionStatus(SubmissionStatus.APPROVED);
        submissionRepository.save(submission);
        log.info("[Admin] Submission #{} acceptée — accusation #{} modifiée", submission.getId(), accusation.getId());
        return accusation.getRapper();
    }

    @Transactional
    public void rejectSubmission(Long submissionId) {
        submissionRepository.findById(submissionId).ifPresent(s -> {
            s.setSubmissionStatus(SubmissionStatus.REJECTED);
            submissionRepository.save(s);
            log.info("[Admin] Submission #{} rejetée", submissionId);
        });
    }

    // ── Withdrawals ───────────────────────────────────────────────────────

    @Transactional
    public void acceptWithdrawal(Long withdrawalId) {
        var wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal #" + withdrawalId + " introuvable"));

        if (wr.getAccusationId() == null)
            throw new IllegalStateException("Withdrawal #" + withdrawalId + " sans accusation liée");

        accusationRepository.deleteById(wr.getAccusationId());
        wr.setStatus(WithdrawalStatus.PROCESSED);
        withdrawalRequestRepository.save(wr);
        log.info("[Admin] Withdrawal #{} accepté — accusation #{} supprimée", withdrawalId, wr.getAccusationId());
    }

    @Transactional
    public void rejectWithdrawal(Long withdrawalId) {
        withdrawalRequestRepository.findById(withdrawalId).ifPresent(w -> {
            w.setStatus(WithdrawalStatus.REJECTED);
            withdrawalRequestRepository.save(w);
            log.info("[Admin] Withdrawal #{} rejeté", withdrawalId);
        });
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private RapperStatus deriveRapperStatus(AccusationStatus accusationStatus) {
        if (accusationStatus == null) return RapperStatus.CONTROVERSY;
        return switch (accusationStatus) {
            case CONVICTED  -> RapperStatus.CONVICTED;
            case ONGOING    -> RapperStatus.ACCUSED;
            default         -> RapperStatus.CONTROVERSY;
        };
    }

    private void recalculateRapperStatus(Rapper rapper) {
        var accusations = accusationRepository.findByRapperId(rapper.getId());
        boolean hasConvicted = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.CONVICTED);
        boolean hasOngoing   = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.ONGOING);

        RapperStatus newStatus = hasConvicted ? RapperStatus.CONVICTED
                : hasOngoing ? RapperStatus.ACCUSED
                : RapperStatus.CONTROVERSY;

        if (rapper.getStatus() != newStatus) {
            rapper.setStatus(newStatus);
            rapperRepository.save(rapper);
            log.info("[Admin] Statut {} recalculé → {}", rapper.getName(), newStatus);
        }
    }
}

