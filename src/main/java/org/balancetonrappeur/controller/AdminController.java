package org.balancetonrappeur.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.config.AdminAuthFilter;
import org.balancetonrappeur.entity.SubmissionStatus;
import org.balancetonrappeur.entity.WithdrawalStatus;
import org.balancetonrappeur.repository.*;
import org.balancetonrappeur.service.AdminService;
import org.balancetonrappeur.service.SpotifyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAuthFilter adminAuthFilter;
    private final AdminService adminService;
    private final SpotifyService spotifyService;
    private final SubmissionRepository submissionRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final RapperRepository rapperRepository;
    private final AccusationRepository accusationRepository;

    @Value("${btr.admin.password:}")
    private String adminPassword;

    // ── Login ─────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String password,
                        HttpServletResponse response,
                        RedirectAttributes ra) {
        if (!adminPassword.isBlank() && password.equals(adminPassword)) {
            var cookie = new Cookie(AdminAuthFilter.COOKIE_NAME, adminAuthFilter.hash(adminPassword));
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(8 * 3600);
            response.addCookie(cookie);
            return "redirect:/admin";
        }
        ra.addFlashAttribute("error", "Mot de passe incorrect.");
        return "redirect:/admin/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        var cookie = new Cookie(AdminAuthFilter.COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/admin/login";
    }

    // ── Dashboard ─────────────────────────────────────────────────────────

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("nbNoSpotify", rapperRepository.countBySpotifyImageUrlIsNull());
        model.addAttribute("nbSubmissions", submissionRepository.countBySubmissionStatus(SubmissionStatus.PENDING));
        model.addAttribute("nbWithdrawals", withdrawalRequestRepository.countByStatus(WithdrawalStatus.PENDING));
        model.addAttribute("nbRappers", rapperRepository.count());
        model.addAttribute("nbAccusations", accusationRepository.count());
        model.addAttribute("pendingSubmissions",
                submissionRepository.findBySubmissionStatusOrderByCreatedAtAsc(SubmissionStatus.PENDING));
        model.addAttribute("pendingWithdrawals",
                withdrawalRequestRepository.findByStatusOrderByCreatedAtAsc(WithdrawalStatus.PENDING));
        model.addAttribute("rappersWithoutSpotify",
                rapperRepository.findBySpotifyImageUrlIsNull());
        return "admin/dashboard";
    }

    // ── Submissions ───────────────────────────────────────────────────────

    @PostMapping("/submissions/{id}/accept")
    public String acceptSubmission(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var rapper = adminService.acceptSubmission(id);
            spotifyService.enrichOnCreate(rapper);
            ra.addFlashAttribute("success", "Submission #" + id + " acceptée.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/submissions/{id}/reject")
    public String rejectSubmission(@PathVariable Long id, RedirectAttributes ra) {
        adminService.rejectSubmission(id);
        ra.addFlashAttribute("success", "Submission #" + id + " rejetée.");
        return "redirect:/admin";
    }

    // ── Withdrawals ───────────────────────────────────────────────────────

    @PostMapping("/withdrawals/{id}/accept")
    public String acceptWithdrawal(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var wr = withdrawalRequestRepository.findById(id).orElseThrow();
            adminService.acceptWithdrawal(id);
            ra.addFlashAttribute("success", "Demande #" + id + " acceptée — accusation supprimée.");
            if (wr.getEmail() != null && !wr.getEmail().isBlank()) {
                ra.addFlashAttribute("mailReminder",
                        "Envoyer un mail à " + wr.getEmail()
                        + " pour l'informer que la demande de retrait concernant "
                        + wr.getRapperName() + " a été acceptée.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/withdrawals/{id}/reject")
    public String rejectWithdrawal(@PathVariable Long id, RedirectAttributes ra) {
        var wr = withdrawalRequestRepository.findById(id).orElse(null);
        adminService.rejectWithdrawal(id);
        ra.addFlashAttribute("success", "Demande #" + id + " rejetée.");
        if (wr != null && wr.getEmail() != null && !wr.getEmail().isBlank()) {
            ra.addFlashAttribute("mailReminder",
                    "Envoyer un mail à " + wr.getEmail()
                    + " pour l'informer que la demande de retrait concernant "
                    + wr.getRapperName() + " a été rejetée.");
        }
        return "redirect:/admin";
    }

    // ── Spotify ───────────────────────────────────────────────────────────

    @PostMapping("/spotify/sync/{rapperId}")
    public String syncSpotify(@PathVariable Long rapperId, RedirectAttributes ra) {
        rapperRepository.findById(rapperId).ifPresent(spotifyService::enrichOnCreate);
        ra.addFlashAttribute("success", "Sync Spotify déclenché pour rappeur #" + rapperId);
        return "redirect:/admin";
    }

    @PostMapping("/spotify/sync-all")
    public String syncAll(RedirectAttributes ra) {
        rapperRepository.findAll().forEach(spotifyService::enrichOnCreate);
        ra.addFlashAttribute("success", "Sync Spotify déclenché pour tous les rappeurs.");
        return "redirect:/admin";
    }
}
