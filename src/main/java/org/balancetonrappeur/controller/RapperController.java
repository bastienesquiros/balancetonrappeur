package org.balancetonrappeur.controller;

import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.entity.RapperStatus;
import org.balancetonrappeur.service.RapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Controller
@RequestMapping("/rappers")
@RequiredArgsConstructor
public class RapperController {

    private static final int PAGE_SIZE = 24;
    private final RapperService rapperService;

    @GetMapping
    public String list(
            @RequestParam(required = false) RapperStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        var pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("name").ascending());
        var result = rapperService.findFiltered(status, pageable);

        var filterParams = status != null ? "&status=" + status.name() : "";

        model.addAttribute("rappers", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("statuses", RapperStatus.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("filterParams", filterParams);
        return "rappers/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam String q, Model model) {
        var results = rapperService.search(q);
        if (results.size() == 1) return "redirect:/rappers/" + results.getFirst().getId();
        if (results.isEmpty()) {
            model.addAttribute("query", q);
            return "rappers/not-found";
        }
        model.addAttribute("rappers", results);
        model.addAttribute("query", q);
        return "rappers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var rapper = rapperService.findByIdWithAccusations(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        var similar = rapperService.findSimilar(id, rapper.getStatus());

        // Date de dernière mise à jour réelle : max(rapper.updatedAt, dernière accusation.updatedAt)
        var lastUpdated = rapper.getAccusations().stream()
                .map(Accusation::getUpdatedAt)
                .max(java.time.LocalDateTime::compareTo)
                .filter(accUpdated -> accUpdated.isAfter(rapper.getUpdatedAt()))
                .orElse(rapper.getUpdatedAt());

        model.addAttribute("rapper", rapper);
        model.addAttribute("lastUpdated", lastUpdated);
        model.addAttribute("similarRappers", similar);
        model.addAttribute("pageTitle", rapper.getName());
        model.addAttribute("pageDescription",
                rapper.getName() + " · " + rapper.getStatus().label() + " · " +
                rapper.getAccusations().size() + " affaire(s) documentée(s) sur Balance Ton Rappeur.");
        model.addAttribute("canonicalUrl", "https://balancetonrappeur.fr/rappers/" + id);
        model.addAttribute("ogType", "profile");
        return "rappers/detail";
    }
}
