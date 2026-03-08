package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.Accusation;
import org.balancetonrappeur.repository.AccusationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TimelineController {

    private final AccusationRepository accusationRepository;

    @GetMapping("/timeline")
    public String timeline(Model model) {
        List<Accusation> all = accusationRepository.findAllForTimeline();

        // Grouper par année DESC
        Map<Integer, List<Accusation>> byYear = new LinkedHashMap<>();
        for (Accusation a : all) {
            int year = a.getFactDate().getYear();
            byYear.computeIfAbsent(year, k -> new java.util.ArrayList<>()).add(a);
        }

        model.addAttribute("byYear", byYear);
        model.addAttribute("pageTitle", "Timeline");
        model.addAttribute("pageDescription",
                "Chronologie de toutes les affaires et scandales du rap documentés sur Balance Ton Rappeur.");
        model.addAttribute("canonicalUrl", "https://balancetonrappeur.fr/timeline");
        return "timeline";
    }
}

