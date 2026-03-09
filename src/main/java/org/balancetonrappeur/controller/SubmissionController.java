package org.balancetonrappeur.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.dto.form.SuggestForm;
import org.balancetonrappeur.dto.form.SubmitForm;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.exception.NoChangeDetectedException;
import org.balancetonrappeur.service.RapperService;
import org.balancetonrappeur.service.SubmissionService;
import org.balancetonrappeur.util.SourceJsonSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/rappers")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;
    private final RapperService rapperService;

    @ModelAttribute("categories")
    public AccusationCategory[] categories() { return AccusationCategory.values(); }

    @ModelAttribute("statuses")
    public AccusationStatus[] statuses() { return AccusationStatus.values(); }

    @ModelAttribute("sourceTypes")
    public SourceType[] sourceTypes() { return SourceType.values(); }

    // ── /suggest ─────────────────────────────────────────────────────────────

    @GetMapping("/suggest")
    public String suggestForm(@RequestParam(required = false) String name, Model model) {
        var form = new SuggestForm();
        if (name != null) form.setRapperName(name);
        model.addAttribute("suggestForm", form);
        model.addAttribute("nameLocked", name != null);
        return "rappers/suggest";
    }

    @PostMapping("/suggest")
    public String suggestSubmit(
            @Valid @ModelAttribute("suggestForm") SuggestForm form,
            BindingResult binding,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (noSourceUrl(form.getSourceUrl())) {
            binding.rejectValue("sourceUrl", "required", "Au moins une source avec une URL est obligatoire.");
        }

        if (binding.hasErrors()) {
            model.addAttribute("nameLocked", false);
            model.addAttribute("prefillSourcesJson",
                    SourceJsonSerializer.toJsonFromParams(form.getSourceType(), form.getSourceTitle(), form.getSourceUrl()));
            model.addAttribute("formHasErrors", true);
            return "rappers/suggest";
        }

        var existing = rapperService.findByNameIgnoreCase(form.getRapperName());
        if (existing.isPresent()) {
            redirectAttributes.addFlashAttribute("info",
                    "\"" + form.getRapperName() + "\" est déjà dans notre base — tu peux ajouter une affaire directement depuis sa fiche.");
            return "redirect:/rappers/" + existing.get().getId();
        }

        submissionService.submitUnknownRapper(form.getRapperName(), form.getCategory(), form.getTitle(),
                form.getStatus(), form.getFactDate(), form.getSourceType(), form.getSourceTitle(), form.getSourceUrl());
        redirectAttributes.addFlashAttribute("submissionSuccess", true);
        return "redirect:/rappers/suggest";
    }

    // ── /{rapperId}/submit ────────────────────────────────────────────────────

    @GetMapping("/{rapperId}/submit")
    public String form(@PathVariable Long rapperId,
                       @RequestParam(defaultValue = "ADD_ACCUSATION") SubmissionType type,
                       @RequestParam(required = false) Long accusationId,
                       Model model) {

        var rapper = rapperService.findByIdWithAccusations(rapperId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        var form = new SubmitForm();
        form.setType(type);
        form.setAccusationId(accusationId);

        if (type == SubmissionType.EDIT_ACCUSATION && accusationId != null) {
            rapperService.findAccusationOnRapper(rapperId, accusationId).ifPresent(a -> {
                form.setCategory(a.getCategory());
                form.setTitle(a.getTitle());
                form.setStatus(a.getStatus());
                form.setFactDate(a.getFactDate());
                model.addAttribute("prefillAccusation", a);
                model.addAttribute("prefillSourcesJson", SourceJsonSerializer.toJson(a.getSources()));
            });
        }

        model.addAttribute("submitForm", form);
        model.addAttribute("rapper", rapper);
        return "rappers/submit";
    }

    @PostMapping("/{rapperId}/submit")
    public String submit(@PathVariable Long rapperId,
                         @Valid @ModelAttribute("submitForm") SubmitForm form,
                         BindingResult binding,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (noSourceUrl(form.getSourceUrl())) {
            binding.rejectValue("sourceUrl", "required", "Au moins une source avec une URL est obligatoire.");
        }

        if (binding.hasErrors()) {
            var rapper = rapperService.findByIdWithAccusations(rapperId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
            model.addAttribute("rapper", rapper);
            model.addAttribute("prefillSourcesJson",
                    SourceJsonSerializer.toJsonFromParams(form.getSourceType(), form.getSourceTitle(), form.getSourceUrl()));
            if (form.getType() == SubmissionType.EDIT_ACCUSATION && form.getAccusationId() != null) {
                rapperService.findAccusationOnRapper(rapperId, form.getAccusationId())
                        .ifPresent(a -> model.addAttribute("prefillAccusation", a));
            }
            model.addAttribute("formHasErrors", true);
            return "rappers/submit";
        }

        try {
            if (form.getType() == SubmissionType.EDIT_ACCUSATION && form.getAccusationId() != null) {
                submissionService.submitEdit(rapperId, form.getAccusationId(), form.getCategory(),
                        form.getTitle(), form.getStatus(), form.getFactDate(),
                        form.getSourceType(), form.getSourceTitle(), form.getSourceUrl());
            } else {
                submissionService.submitAdd(rapperId, form.getCategory(), form.getTitle(),
                        form.getStatus(), form.getFactDate(),
                        form.getSourceType(), form.getSourceTitle(), form.getSourceUrl());
            }
            redirectAttributes.addFlashAttribute("submissionSuccess", true);
            return "redirect:/rappers/" + rapperId;
        } catch (NoChangeDetectedException e) {
            var rapper = rapperService.findByIdWithAccusations(rapperId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));
            model.addAttribute("rapper", rapper);
            model.addAttribute("prefillSourcesJson",
                    SourceJsonSerializer.toJsonFromParams(form.getSourceType(), form.getSourceTitle(), form.getSourceUrl()));
            if (form.getType() == SubmissionType.EDIT_ACCUSATION && form.getAccusationId() != null) {
                rapperService.findAccusationOnRapper(rapperId, form.getAccusationId())
                        .ifPresent(a -> model.addAttribute("prefillAccusation", a));
            }
            model.addAttribute("submissionError", "Aucune modification détectée — le formulaire est identique à l'affaire existante.");
            return "rappers/submit";
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private boolean noSourceUrl(java.util.List<String> urls) {
        return urls == null || urls.stream().allMatch(u -> u == null || u.isBlank());
    }
}
