package org.balancetonrappeur.controller;

import org.balancetonrappeur.service.RapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RapperService rapperService;

    @GetMapping("/")
    public String home(Model model) {
        var stats = rapperService.getHomeStats();
        model.addAttribute("recentRappers",   stats.recentRappers());
        model.addAttribute("rapperCount",     stats.rapperCount());
        model.addAttribute("convictedCount",  stats.convictedCount());
        model.addAttribute("accusedCount",    stats.accusedCount());
        model.addAttribute("accusationCount", stats.accusationCount());
        return "home";
    }
}
