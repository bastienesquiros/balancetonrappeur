package org.balancetonrappeur.service;

import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.entity.SubmissionStatus;
import org.balancetonrappeur.entity.WithdrawalStatus;
import org.balancetonrappeur.repository.SubmissionRepository;
import org.balancetonrappeur.repository.WithdrawalRequestRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@Service
public class DigestMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final SubmissionRepository submissionRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final String digestTo;
    private final String from;

    public DigestMailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            SubmissionRepository submissionRepository,
            WithdrawalRequestRepository withdrawalRequestRepository,
            @Value("${btr.mail.digest-to}") String digestTo,
            @Value("${btr.mail.from}") String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.submissionRepository = submissionRepository;
        this.withdrawalRequestRepository = withdrawalRequestRepository;
        this.digestTo = digestTo;
        this.from = from;
    }

    // Tous les jours à 8h00
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyDigest() {
        var pendingSubs = submissionRepository.findBySubmissionStatusOrderByCreatedAtAsc(SubmissionStatus.PENDING);
        var pendingWith = withdrawalRequestRepository.findByStatusOrderByCreatedAtAsc(WithdrawalStatus.PENDING);

        if (pendingSubs.isEmpty() && pendingWith.isEmpty()) {
            log.info("[Digest] Rien en attente, mail non envoyé.");
            return;
        }

        try {
            var ctx = new Context(Locale.FRENCH);
            ctx.setVariable("submissions", pendingSubs);
            ctx.setVariable("withdrawals", pendingWith);

            var html = templateEngine.process("mail/digest", ctx);

            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(digestTo);
            helper.setSubject("📋 BTR Digest — " + pendingSubs.size() + " soumission(s) · "
                    + pendingWith.size() + " retrait(s) · "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            helper.setText(html, true);

            mailSender.send(message);
            log.info("[Digest] Mail envoyé — {} soumissions, {} retraits", pendingSubs.size(), pendingWith.size());
        } catch (Exception e) {
            log.error("[Digest] Erreur envoi mail : {}", e.getMessage());
        }
    }
}
