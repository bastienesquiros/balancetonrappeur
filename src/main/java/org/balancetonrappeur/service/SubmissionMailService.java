package org.balancetonrappeur.service;

import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.entity.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Slf4j
@Service
public class SubmissionMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String from;

    public SubmissionMailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${btr.mail.noreply}") String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
    }

    /** Envoyé immédiatement à la soumission (si email fourni). */
    public void sendConfirmation(Submission submission) {
        if (!hasEmail(submission)) return;
        send(submission.getSubmitterEmail(),
             "Votre contribution a bien été reçue — Balance Ton Rappeur",
             "mail/submission-received",
             buildContext(submission, null));
    }

    /** Envoyé lors de l'acceptation par l'admin. */
    public void sendAccepted(Submission submission) {
        if (!hasEmail(submission)) return;
        send(submission.getSubmitterEmail(),
             "Votre contribution a été acceptée — Balance Ton Rappeur",
             "mail/submission-accepted",
             buildContext(submission, null));
    }

    /** Envoyé lors du rejet par l'admin. */
    public void sendRejected(Submission submission, String reason) {
        if (!hasEmail(submission)) return;
        send(submission.getSubmitterEmail(),
             "Votre contribution n'a pas pu être publiée — Balance Ton Rappeur",
             "mail/submission-rejected",
             buildContext(submission, reason));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private boolean hasEmail(Submission submission) {
        return submission.getSubmitterEmail() != null && !submission.getSubmitterEmail().isBlank();
    }

    private Context buildContext(Submission submission, String reason) {
        var ctx = new Context(Locale.FRENCH);
        ctx.setVariable("submission", submission);
        ctx.setVariable("rapperName", rapperName(submission));
        ctx.setVariable("reason", reason);
        return ctx;
    }

    private String rapperName(Submission submission) {
        if (submission.getRapper() != null) return submission.getRapper().getName();
        if (submission.getUnknownRapperName() != null) return submission.getUnknownRapperName();
        return "inconnu";
    }

    private void send(String to, String subject, String template, Context ctx) {
        try {
            var html = templateEngine.process(template, ctx);
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("[SubmissionMail] Mail '{}' envoyé à {}", template, to);
        } catch (Exception e) {
            log.error("[SubmissionMail] Erreur envoi '{}' à {} : {}", template, to, e.getMessage());
        }
    }
}

