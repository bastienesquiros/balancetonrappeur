package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.service.StatsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/stats")
    public String stats(Model model) {
        model.addAttribute("stats", statsService.compute());
        return "stats";
    }
}

