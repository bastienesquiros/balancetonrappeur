package org.balancetonrappeur.service;

import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.entity.WithdrawalRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;

@Slf4j
@Service
public class WithdrawalMailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String from;

    public WithdrawalMailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${btr.mail.noreply}") String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
    }

    /** Envoyé lors de l'acceptation (suppression de l'affaire). */
    public void sendAccepted(WithdrawalRequest wr, String comment) {
        if (wr.getEmail() == null || wr.getEmail().isBlank()) return;
        send(wr.getEmail(),
             "Votre demande de retrait a été acceptée — Balance Ton Rappeur",
             "mail/withdrawal-accepted",
             buildContext(wr, comment));
    }

    /** Envoyé lors du rejet de la demande. */
    public void sendRejected(WithdrawalRequest wr, String reason) {
        if (wr.getEmail() == null || wr.getEmail().isBlank()) return;
        send(wr.getEmail(),
             "Votre demande de retrait n'a pas pu être traitée — Balance Ton Rappeur",
             "mail/withdrawal-rejected",
             buildContext(wr, reason));
    }

    // ── helpers ───────────────────────────────────────────────────────────────


    private Context buildContext(WithdrawalRequest wr, String comment) {
        var ctx = new Context(Locale.FRENCH);
        ctx.setVariable("withdrawal", wr);
        ctx.setVariable("rapperName", wr.getRapperName() != null ? wr.getRapperName() : "inconnu");
        ctx.setVariable("accusationTitle", wr.getAccusationTitle() != null ? wr.getAccusationTitle() : "—");
        ctx.setVariable("comment", comment);
        return ctx;
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
            log.info("[WithdrawalMail] Mail '{}' envoyé à {}", template, to);
        } catch (Exception e) {
            log.error("[WithdrawalMail] Erreur envoi '{}' à {} : {}", template, to, e.getMessage());
        }
    }
}

