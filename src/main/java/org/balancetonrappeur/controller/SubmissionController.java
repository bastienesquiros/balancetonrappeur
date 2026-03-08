package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.service.RapperService;
import org.balancetonrappeur.service.SubmissionService;
import org.balancetonrappeur.exception.NoChangeDetectedException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final RapperService rapperService;

    // ─── Rappeur non indexé (vient du not-found) ───────────────────────────
    @GetMapping("/rappers/suggest")
    public String suggestForm(@RequestParam(required = false) String name, Model model) {
        model.addAttribute("unknownRapperName", name);
        model.addAttribute("categories", AccusationCategory.values());
        model.addAttribute("statuses", AccusationStatus.values());
        model.addAttribute("sourceTypes", SourceType.values());
        return "rappers/suggest";
    }

    @PostMapping("/rappers/suggest")
    public String suggestSubmit(
            @RequestParam String rapperName,
            @RequestParam AccusationCategory category,
            @RequestParam String title,
            @RequestParam AccusationStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate factDate,
            @RequestParam List<SourceType> sourceType,
            @RequestParam List<String> sourceTitle,
            @RequestParam List<String> sourceUrl,
            RedirectAttributes redirectAttributes
    ) {
        // Si le rappeur existe déjà en base → rediriger vers sa fiche
        var existing = rapperService.findByNameIgnoreCase(rapperName);
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("info",
                    "\"" + rapperName + "\" est déjà dans notre base — tu peux ajouter une affaire directement depuis sa fiche.");
            return "redirect:/rappers/" + existing.get().getId();
        }

        submissionService.submitUnknownRapper(rapperName, category, title, status, factDate,
                sourceType, sourceTitle, sourceUrl);
        redirectAttributes.addFlashAttribute("submissionSuccess", true);
        return "redirect:/rappers/suggest";
    }

    // ─── Rappeur indexé (add ou edit) ──────────────────────────────────────
    @GetMapping("/rappers/{rapperId}/submit")
    public String form(@PathVariable Long rapperId,
                       @RequestParam(defaultValue = "ADD_ACCUSATION") SubmissionType type,
                       @RequestParam(required = false) Long accusationId,
                       Model model) {

        var rapper = rapperService.findByIdWithAccusations(rapperId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        model.addAttribute("rapper", rapper);
        model.addAttribute("type", type);
        model.addAttribute("categories", AccusationCategory.values());
        model.addAttribute("statuses", AccusationStatus.values());
        model.addAttribute("sourceTypes", SourceType.values());

        if (type == SubmissionType.EDIT_ACCUSATION && accusationId != null) {
            rapper.getAccusations().stream()
                    .filter(a -> a.getId().equals(accusationId))
                    .findFirst()
                    .ifPresent(a -> {
                        model.addAttribute("prefillAccusation", a);
                        // JSON des sources pour Alpine.js
                        var json = new StringBuilder("[");
                        var sources = a.getSources();
                        int i = 0;
                        for (var s : sources) {
                            if (i++ > 0) json.append(",");
                            json.append("{\"type\":\"").append(s.getType().name()).append("\"")
                                .append(",\"title\":\"").append(escape(s.getTitle())).append("\"")
                                .append(",\"url\":\"").append(escape(s.getUrl())).append("\"}");
                        }
                        json.append("]");
                        model.addAttribute("prefillSourcesJson", json.toString());
                    });
        }

        return "rappers/submit";
    }

    @PostMapping("/rappers/{rapperId}/submit")
    public String submit(@PathVariable Long rapperId,
                         @RequestParam SubmissionType type,
                         @RequestParam(required = false) Long accusationId,
                         @RequestParam AccusationCategory category,
                         @RequestParam String title,
                         @RequestParam AccusationStatus status,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate factDate,
                         @RequestParam List<SourceType> sourceType,
                         @RequestParam List<String> sourceTitle,
                         @RequestParam List<String> sourceUrl,
                         RedirectAttributes redirectAttributes) {

        try {
            if (type == SubmissionType.EDIT_ACCUSATION && accusationId != null) {
                submissionService.submitEdit(rapperId, accusationId, category, title, status, factDate,
                        sourceType, sourceTitle, sourceUrl);
            } else {
                submissionService.submitAdd(rapperId, category, title, status, factDate,
                        sourceType, sourceTitle, sourceUrl);
            }
            redirectAttributes.addFlashAttribute("submissionSuccess", true);
        } catch (NoChangeDetectedException e) {
            redirectAttributes.addFlashAttribute("submissionError", "Aucune modification détectée — le formulaire est identique à l'affaire existante.");
        }

        return "redirect:/rappers/" + rapperId + "/submit"
                + "?type=" + type.name()
                + (accusationId != null ? "&accusationId=" + accusationId : "");
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }
}
