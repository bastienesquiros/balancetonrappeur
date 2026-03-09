package org.balancetonrappeur.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balancetonrappeur.dto.api.ScanResultDto;
import org.balancetonrappeur.service.ScanService;
import org.balancetonrappeur.spotify.client.SpotifyUserClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/scan")
@RequiredArgsConstructor
public class ScanController {

    private static final String SESSION_TOKEN        = "spotify_access_token";
    private static final String SESSION_STATE        = "spotify_oauth_state";
    private static final String SESSION_REMOVE_TOKEN = "spotify_remove_token";
    private static final String SESSION_RESULTS      = "spotify_scan_results";

    private final SpotifyUserClient spotifyUserClient;
    private final ScanService scanService;

    /** Page d'accueil du scan. */
    @GetMapping
    public String scanPage(Model model, HttpSession session) {
        // Uniquement le token frais (post-OAuth) déclenche le redirect — pas le remove token résiduel
        if (session.getAttribute(SESSION_TOKEN) != null) {
            return "redirect:/scan/result";
        }
        // Nettoyage du remove token si l'utilisateur revient sur /scan
        session.removeAttribute(SESSION_REMOVE_TOKEN);
        session.removeAttribute(SESSION_RESULTS);
        model.addAttribute("configured", spotifyUserClient.isConfigured());
        return "scan/scan";
    }

    /** Redirige l'utilisateur vers Spotify pour autorisation. */
    @GetMapping("/connect")
    public String connect(HttpSession session) {
        if (!spotifyUserClient.isConfigured()) return "redirect:/scan";
        String state = UUID.randomUUID().toString();
        session.setAttribute(SESSION_STATE, state);
        return "redirect:" + spotifyUserClient.buildAuthorizationUrl(state);
    }

    /** Callback OAuth2 Spotify. */
    @GetMapping("/callback")
    public String callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (error != null) {
            log.warn("[Scan] Accès Spotify refusé : {}", error);
            redirectAttributes.addFlashAttribute("scanError", "Autorisation Spotify refusée.");
            return "redirect:/scan";
        }

        String expectedState = (String) session.getAttribute(SESSION_STATE);
        if (state == null || !state.equals(expectedState)) {
            log.warn("[Scan] State OAuth invalide");
            redirectAttributes.addFlashAttribute("scanError", "Erreur de sécurité, veuillez réessayer.");
            return "redirect:/scan";
        }

        var tokenOpt = spotifyUserClient.exchangeCode(code);
        if (tokenOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("scanError", "Impossible d'obtenir le token Spotify.");
            return "redirect:/scan";
        }

        session.setAttribute(SESSION_TOKEN, tokenOpt.get().accessToken());
        session.removeAttribute(SESSION_STATE);
        return "redirect:/scan/loading";
    }

    /** Page de chargement — affiché pendant que le scan tourne. */
    @GetMapping("/loading")
    public String loading(HttpSession session) {
        if (session.getAttribute(SESSION_TOKEN) == null) return "redirect:/scan";
        return "scan-loading";
    }

    /** Lance le scan et affiche les résultats. */
    @SuppressWarnings("unchecked")
    @GetMapping("/result")
    public String result(HttpSession session, Model model) {
        var cached = (List<ScanResultDto>) session.getAttribute(SESSION_RESULTS);
        String token = (String) session.getAttribute(SESSION_TOKEN);

        // Résultats déjà en session (après un retrait) — pas besoin de re-scanner
        if (cached != null && token == null) {
            model.addAttribute("results", cached);
            model.addAttribute("clean", cached.isEmpty());
            return "scan-result";
        }

        String activeToken = token != null ? token : (String) session.getAttribute(SESSION_REMOVE_TOKEN);
        if (activeToken == null) return "redirect:/scan";

        var scanResult = scanService.scan(activeToken);
        var results = scanResult.results();
        // Consommer le token principal, garder le remove token + résultats en session
        if (token != null) {
            session.removeAttribute(SESSION_TOKEN);
            session.setAttribute(SESSION_REMOVE_TOKEN, token);
        }
        session.setAttribute(SESSION_RESULTS, results);

        model.addAttribute("results", results);
        model.addAttribute("clean", results.isEmpty());
        model.addAttribute("playlistsUnavailable", scanResult.playlistsUnavailable());
        return "scan-result";
    }

    /** Retire un titre (liked ou playlist). */
    @SuppressWarnings("unchecked")
    @PostMapping("/remove")
    public String remove(
            @RequestParam String trackUri,
            @RequestParam(required = false) String playlistId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute(SESSION_REMOVE_TOKEN);
        if (token == null) {
            redirectAttributes.addFlashAttribute("scanError", "Session expirée, veuillez relancer le scan.");
            return "redirect:/scan";
        }

        boolean isPlaylist = playlistId != null && !playlistId.isBlank();
        boolean success = isPlaylist
                ? spotifyUserClient.removePlaylistTrack(token, playlistId, trackUri)
                : spotifyUserClient.removeLikedTrack(token, trackUri);

        if (success) {
            var cached = (List<ScanResultDto>) session.getAttribute(SESSION_RESULTS);
            if (cached != null) {
                String effectivePlaylistId = isPlaylist ? playlistId : null;
                var updated = cached.stream()
                        .map(r -> new ScanResultDto(
                                r.rapperId(), r.rapperName(), r.rapperImageUrl(), r.rapperStatus(),
                                r.tracks().stream()
                                        .filter(t -> !(t.trackUri().equals(trackUri)
                                                && Objects.equals(t.playlistId(), effectivePlaylistId)))
                                        .toList(),
                                r.accusations()
                        ))
                        .filter(r -> !r.tracks().isEmpty())
                        .toList();
                session.setAttribute(SESSION_RESULTS, updated);
            }
            redirectAttributes.addFlashAttribute("removeSuccess", "Titre retiré de Spotify.");
        } else {
            redirectAttributes.addFlashAttribute("scanError", "Erreur lors du retrait sur Spotify, veuillez réessayer.");
        }
        return "redirect:/scan/result";
    }

    /** Déconnecte la session Spotify. */
    @PostMapping("/disconnect")
    public String disconnect(HttpSession session) {
        session.removeAttribute(SESSION_TOKEN);
        session.removeAttribute(SESSION_REMOVE_TOKEN);
        session.removeAttribute(SESSION_RESULTS);
        session.removeAttribute(SESSION_STATE);
        return "redirect:/scan";
    }
}
