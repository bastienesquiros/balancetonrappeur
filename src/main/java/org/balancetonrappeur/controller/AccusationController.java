package org.balancetonrappeur.controller;

import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.balancetonrappeur.service.AccusationService;
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
@RequestMapping("/accusations")
@RequiredArgsConstructor
public class AccusationController {

    private static final int PAGE_SIZE = 20;
    private final AccusationService accusationService;

    @GetMapping
    public String list(
            @RequestParam(required = false) AccusationCategory category,
            @RequestParam(required = false) AccusationStatus status,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        var pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("factDate").descending());
        var result = category != null
                ? accusationService.findByCategory(category, pageable)
                : status != null
                        ? accusationService.findByStatus(status, pageable)
                        : accusationService.findAll(pageable);

        model.addAttribute("accusations", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("categories", AccusationCategory.values());
        model.addAttribute("statuses", AccusationStatus.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        return "accusations/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        var accusation = accusationService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND));

        model.addAttribute("accusation", accusation);
        model.addAttribute("pageTitle", accusation.getTitle());
        model.addAttribute("pageDescription",
                accusation.getRapper().getName() + " · " + accusation.getTitle());
        model.addAttribute("canonicalUrl", "https://balancetonrappeur.fr/accusations/" + id);
        return "accusations/detail";
    }
}
