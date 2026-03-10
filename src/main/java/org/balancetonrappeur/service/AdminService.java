package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.dto.view.AdminDashboardDto;
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
    private final SubmissionMailService submissionMailService;
    private final WithdrawalMailService withdrawalMailService;

    // Dashboard

    public AdminDashboardDto getDashboard() {
        return new AdminDashboardDto(
            rapperRepository.count(),
            accusationRepository.count(),
            submissionRepository.countBySubmissionStatus(SubmissionStatus.PENDING),
            withdrawalRequestRepository.countByStatus(WithdrawalStatus.PENDING),
            rapperRepository.countBySpotifyImageUrlIsNull(),
            submissionRepository.findBySubmissionStatusOrderByCreatedAtAsc(SubmissionStatus.PENDING),
            withdrawalRequestRepository.findByStatusOrderByCreatedAtAsc(WithdrawalStatus.PENDING),
            rapperRepository.findBySpotifyImageUrlIsNull()
        );
    }

    // Submissions

    @Transactional
    public Rapper acceptSubmission(Long submissionId) {
        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission #" + submissionId + " introuvable"));

        return switch (submission.getType()) {
            case ADD_ACCUSATION -> acceptAddAccusation(submission);
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
        submissionMailService.sendAccepted(submission);
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
        submissionMailService.sendAccepted(submission);
        log.info("[Admin] Submission #{} acceptée — accusation #{} modifiée", submission.getId(), accusation.getId());
        return accusation.getRapper();
    }

    @Transactional
    public void rejectSubmission(Long submissionId, String rejectionReason) {
        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission #" + submissionId + " introuvable"));
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);
        submissionRepository.save(submission);
        submissionMailService.sendRejected(submission, rejectionReason);
        log.info("[Admin] Submission #{} rejetée", submissionId);
    }

    // Withdrawals

    /**
     * Accepte la demande de retrait et envoie un mail de confirmation si email fourni.
     */
    @Transactional
    public void acceptWithdrawal(Long withdrawalId, String comment) {
        var wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal #" + withdrawalId + " introuvable"));

        var accusation = wr.getAccusation();
        if (accusation == null)
            throw new IllegalStateException("Withdrawal #" + withdrawalId + " sans accusation liée");

        long accusationId = accusation.getId();
        accusationRepository.deleteById(accusationId);
        wr.setStatus(WithdrawalStatus.PROCESSED);
        withdrawalRequestRepository.save(wr);
        log.info("[Admin] Withdrawal #{} accepté — accusation #{} supprimée", withdrawalId, accusationId);

        withdrawalMailService.sendAccepted(wr, comment);
    }

    /**
     * Rejette la demande de retrait et envoie un mail si email fourni.
     */
    @Transactional
    public void rejectWithdrawal(Long withdrawalId, String reason) {
        var wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal #" + withdrawalId + " introuvable"));
        wr.setStatus(WithdrawalStatus.REJECTED);
        withdrawalRequestRepository.save(wr);
        log.info("[Admin] Withdrawal #{} rejeté", withdrawalId);

        withdrawalMailService.sendRejected(wr, reason);
    }

    // Helpers


    private RapperStatus deriveRapperStatus(AccusationStatus accusationStatus) {
        if (accusationStatus == null) return RapperStatus.ACCUSED;
        return switch (accusationStatus) {
            case CONVICTED -> RapperStatus.CONVICTED;
            case ONGOING   -> RapperStatus.ONGOING;
            case ACQUITTED -> RapperStatus.ACQUITTED;
        };
    }

    private void recalculateRapperStatus(Rapper rapper) {
        var accusations = accusationRepository.findByRapperId(rapper.getId());
        boolean hasConvicted = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.CONVICTED);
        boolean hasOngoing   = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.ONGOING);
        boolean hasAcquitted = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.ACQUITTED);

        RapperStatus newStatus = hasConvicted ? RapperStatus.CONVICTED
                : hasOngoing   ? RapperStatus.ONGOING
                : hasAcquitted ? RapperStatus.ACQUITTED
                : RapperStatus.ACCUSED;

        if (rapper.getStatus() != newStatus) {
            rapper.setStatus(newStatus);
            rapperRepository.save(rapper);
            log.info("[Admin] Statut {} recalculé → {}", rapper.getName(), newStatus);
        }
    }
}

