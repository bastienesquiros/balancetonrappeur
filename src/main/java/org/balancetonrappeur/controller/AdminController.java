package org.balancetonrappeur.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.config.AdminAuthFilter;
import org.balancetonrappeur.service.AdminService;
import org.balancetonrappeur.service.RapperService;
import org.balancetonrappeur.service.SpotifyService;
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
    private final RapperService rapperService;
    private final SpotifyService spotifyService;


    // Login

    @GetMapping("/login")
    public String loginPage() { return "admin/login"; }

    @PostMapping("/login")
    public String login(@RequestParam String password, HttpServletRequest request, HttpServletResponse response, RedirectAttributes ra) {
        if (adminAuthFilter.checkPassword(password)) {
            response.addCookie(adminAuthFilter.createAuthCookie(request));
            return "redirect:/admin";
        }
        ra.addFlashAttribute("error", "Mot de passe incorrect.");
        return "redirect:/admin/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        response.addCookie(adminAuthFilter.createLogoutCookie());
        return "redirect:/admin/login";
    }

    // Dashboard

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("dashboard", adminService.getDashboard());
        return "admin/dashboard";
    }

    // Submissions

    @PostMapping("/submissions/{id}/accept")
    public String acceptSubmission(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var rapper = adminService.acceptSubmission(id);
            spotifyService.syncSpotify(rapper);
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

    // Withdrawals

    @PostMapping("/withdrawals/{id}/accept")
    public String acceptWithdrawal(@PathVariable Long id, RedirectAttributes ra) {
        try {
            var mailReminder = adminService.acceptWithdrawal(id);
            ra.addFlashAttribute("success", "Demande #" + id + " acceptée — accusation supprimée.");
            mailReminder.ifPresent(m -> ra.addFlashAttribute("mailReminder", m));
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/withdrawals/{id}/reject")
    public String rejectWithdrawal(@PathVariable Long id, RedirectAttributes ra) {
        var mailReminder = adminService.rejectWithdrawal(id);
        ra.addFlashAttribute("success", "Demande #" + id + " rejetée.");
        mailReminder.ifPresent(m -> ra.addFlashAttribute("mailReminder", m));
        return "redirect:/admin";
    }

    // Spotify

    @PostMapping("/spotify/sync/{rapperId}")
    public String syncSpotify(@PathVariable Long rapperId, RedirectAttributes ra) {
        rapperService.findByIdWithAccusations(rapperId).ifPresent(spotifyService::syncSpotify);
        ra.addFlashAttribute("success", "Sync Spotify déclenché pour rappeur #" + rapperId);
        return "redirect:/admin";
    }

    @PostMapping("/spotify/sync-all")
    public String syncAll(RedirectAttributes ra) {
        rapperService.findAll().forEach(spotifyService::syncSpotify);
        ra.addFlashAttribute("success", "Sync Spotify déclenché pour tous les rappeurs.");
        return "redirect:/admin";
    }
}
