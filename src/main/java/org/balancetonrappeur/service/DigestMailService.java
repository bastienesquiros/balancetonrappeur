package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class DigestMailService {

    private final JavaMailSender mailSender;
    private final SubmissionRepository submissionRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;

    @Value("${btr.mail.digest-to}")
    private String digestTo;

    @Value("${btr.mail.from}")
    private String from;

    // Tous les jours à 8h00
    @Scheduled(cron = "0 0 8 * * *")
    public void sendDailyDigest() {
        var since = LocalDateTime.now().minusHours(24);
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        var submissions  = submissionRepository.findByStatusSince(SubmissionStatus.PENDING, since);
        var withdrawals  = withdrawalRequestRepository.findByStatusSince(WithdrawalStatus.PENDING, since);
        var totalPendingSubs  = submissionRepository.countBySubmissionStatus(SubmissionStatus.PENDING);
        var totalPendingWith  = withdrawalRequestRepository.countByStatus(WithdrawalStatus.PENDING);

        // Pas de mail si rien de nouveau
        if (submissions.isEmpty() && withdrawals.isEmpty()) {
            log.info("[Digest] Rien de nouveau sur les dernières 24h, mail non envoyé.");
            return;
        }

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(digestTo);
            helper.setSubject("📋 BTR Digest — " + submissions.size() + " soumission(s) · "
                    + withdrawals.size() + " retrait(s) · "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            helper.setText(buildHtml(submissions, withdrawals, totalPendingSubs, totalPendingWith, formatter), true);

            mailSender.send(message);
            log.info("[Digest] Mail envoyé — {} soumissions, {} retraits", submissions.size(), withdrawals.size());
        } catch (Exception e) {
            log.error("[Digest] Erreur envoi mail : {}", e.getMessage());
        }
    }

    private String buildHtml(
            java.util.List<org.balancetonrappeur.entity.Submission> submissions,
            java.util.List<org.balancetonrappeur.entity.WithdrawalRequest> withdrawals,
            long totalPendingSubs, long totalPendingWith,
            DateTimeFormatter fmt) {

        var sb = new StringBuilder();
        sb.append("""
            <html><body style="font-family: system-ui, sans-serif; background:#0f0f0f; color:#e4e4e7; padding:32px; max-width:640px; margin:0 auto;">
            <h2 style="color:#fff; font-size:20px; margin-bottom:4px;">📋 Balance Ton Rappeur — Digest quotidien</h2>
            <p style="color:#71717a; font-size:13px; margin-bottom:32px;">Dernières 24h · <strong style="color:#a1a1aa;">""")
             .append(totalPendingSubs).append("</strong> soumission(s) en attente · <strong style=\"color:#a1a1aa;\">")
             .append(totalPendingWith).append("</strong> retrait(s) en attente</p>");

        // ── Soumissions ────────────────────────────────────────────────────
        if (!submissions.isEmpty()) {
            sb.append("<h3 style=\"color:#fff; font-size:15px; border-bottom:1px solid #27272a; padding-bottom:8px; margin-bottom:16px;\">")
              .append("🆕 Nouvelles soumissions (").append(submissions.size()).append(")</h3>");

            for (var s : submissions) {
                var rapperDisplay = s.getRapper() != null ? s.getRapper().getName() : s.getUnknownRapperName();
                var type = s.getType().name().equals("ADD_ACCUSATION") ? "Ajout" : "Modification";
                sb.append("<div style=\"background:#18181b; border:1px solid #27272a; border-radius:10px; padding:14px 16px; margin-bottom:10px;\">")
                  .append("<p style=\"margin:0 0 6px; font-size:13px; color:#a1a1aa;\">")
                  .append(type).append(" · ").append(s.getCreatedAt().format(fmt)).append("</p>")
                  .append("<p style=\"margin:0 0 4px; font-size:15px; font-weight:600; color:#fff;\">").append(escapeHtml(s.getTitle())).append("</p>")
                  .append("<p style=\"margin:0; font-size:13px; color:#71717a;\">Rappeur : ").append(escapeHtml(rapperDisplay != null ? rapperDisplay : "?")).append("</p>")
                  .append("</div>");
            }
        }

        // ── Retraits ───────────────────────────────────────────────────────
        if (!withdrawals.isEmpty()) {
            sb.append("<h3 style=\"color:#fff; font-size:15px; border-bottom:1px solid #27272a; padding-bottom:8px; margin-bottom:16px; margin-top:28px;\">")
              .append("⚠️ Demandes de retrait (").append(withdrawals.size()).append(")</h3>");

            for (var w : withdrawals) {
                sb.append("<div style=\"background:#18181b; border:1px solid #3f1a1a; border-radius:10px; padding:14px 16px; margin-bottom:10px;\">")
                  .append("<p style=\"margin:0 0 6px; font-size:13px; color:#a1a1aa;\">")
                  .append(w.getCreatedAt().format(fmt)).append(" · ").append(w.getReason().name()).append("</p>")
                  .append("<p style=\"margin:0 0 4px; font-size:15px; font-weight:600; color:#fff;\">")
                  .append(escapeHtml(w.getRapperName() != null ? w.getRapperName() : "—")).append("</p>");
                if (w.getAccusationTitle() != null) {
                    sb.append("<p style=\"margin:0 0 4px; font-size:13px; color:#71717a;\">Affaire : ").append(escapeHtml(w.getAccusationTitle())).append("</p>");
                }
                sb.append("<p style=\"margin:4px 0 0; font-size:13px; color:#a1a1aa; font-style:italic;\">")
                  .append(escapeHtml(w.getMessage())).append("</p>");
                if (w.getEmail() != null) {
                    sb.append("<p style=\"margin:4px 0 0; font-size:12px; color:#52525b;\">Contact : ").append(escapeHtml(w.getEmail())).append("</p>");
                }
                sb.append("</div>");
            }
        }

        sb.append("<p style=\"color:#3f3f46; font-size:11px; margin-top:32px;\">Balance Ton Rappeur · digest automatique quotidien</p>");
        sb.append("</body></html>");
        return sb.toString();
    }

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}

