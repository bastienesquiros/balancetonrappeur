package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.service.AccusationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class TimelineController {

    private final AccusationService accusationService;

    @GetMapping("/timeline")
    public String timeline(Model model) {
        model.addAttribute("byYear", accusationService.findGroupedByYear());
        model.addAttribute("pageTitle", "Timeline");
        model.addAttribute("pageDescription",
                "Chronologie de toutes les affaires du rap documentées sur Balance Ton Rappeur.");
        model.addAttribute("canonicalUrl", "https://balancetonrappeur.fr/timeline");
        return "timeline";
    }
}
