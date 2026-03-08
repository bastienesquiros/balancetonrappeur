package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.dto.AdminDashboardDto;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final SubmissionRepository submissionRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final RapperRepository rapperRepository;
    private final AccusationRepository accusationRepository;

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
        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission #" + submissionId + " introuvable"));
        submission.setSubmissionStatus(SubmissionStatus.REJECTED);
        submissionRepository.save(submission);
        log.info("[Admin] Submission #{} rejetée", submissionId);
    }

    // Withdrawals

    /**
     * Accepte et retourne optionnellement un message de reminder mail.
     */
    @Transactional
    public Optional<String> acceptWithdrawal(Long withdrawalId) {
        var wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal #" + withdrawalId + " introuvable"));

        if (wr.getAccusationId() == null)
            throw new IllegalStateException("Withdrawal #" + withdrawalId + " sans accusation liée");

        accusationRepository.deleteById(wr.getAccusationId());
        wr.setStatus(WithdrawalStatus.PROCESSED);
        withdrawalRequestRepository.save(wr);
        log.info("[Admin] Withdrawal #{} accepté — accusation #{} supprimée", withdrawalId, wr.getAccusationId());

        return buildMailReminder(wr, "acceptée");
    }

    /**
     * Rejette et retourne optionnellement un message de reminder mail.
     */
    @Transactional
    public Optional<String> rejectWithdrawal(Long withdrawalId) {
        var wr = withdrawalRequestRepository.findById(withdrawalId)
                .orElseThrow(() -> new IllegalArgumentException("Withdrawal #" + withdrawalId + " introuvable"));
        wr.setStatus(WithdrawalStatus.REJECTED);
        withdrawalRequestRepository.save(wr);
        log.info("[Admin] Withdrawal #{} rejeté", withdrawalId);
        return buildMailReminder(wr, "rejetée");
    }

    // Helpers

    private Optional<String> buildMailReminder(WithdrawalRequest wr, String action) {
        if (wr.getEmail() == null || wr.getEmail().isBlank()) return Optional.empty();
        return Optional.of("Envoyer un mail à " + wr.getEmail()
                + " pour l'informer que la demande de retrait concernant "
                + wr.getRapperName() + " a été " + action + ".");
    }

    private RapperStatus deriveRapperStatus(AccusationStatus accusationStatus) {
        if (accusationStatus == null) return RapperStatus.CONTROVERSY;
        return switch (accusationStatus) {
            case CONVICTED -> RapperStatus.CONVICTED;
            case ONGOING -> RapperStatus.ACCUSED;
            default -> RapperStatus.CONTROVERSY;
        };
    }

    private void recalculateRapperStatus(Rapper rapper) {
        var accusations = accusationRepository.findByRapperId(rapper.getId());
        boolean hasConvicted = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.CONVICTED);
        boolean hasOngoing = accusations.stream().anyMatch(a -> a.getStatus() == AccusationStatus.ONGOING);

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

