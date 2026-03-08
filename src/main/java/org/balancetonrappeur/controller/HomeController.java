package org.balancetonrappeur.controller;

import org.balancetonrappeur.entity.RapperStatus;
import org.balancetonrappeur.service.AccusationService;
import org.balancetonrappeur.service.RapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final RapperService rapperService;
    private final AccusationService accusationService;

    @GetMapping("/")
    public String home(Model model) {
        // 8 plus récents pour la home
        var recentRappers = rapperService.findAll(
                PageRequest.of(0, 8, Sort.by("createdAt").descending())
        ).getContent();

        model.addAttribute("recentRappers", recentRappers);
        model.addAttribute("rapperCount",    rapperService.findAll().size());
        model.addAttribute("convictedCount", rapperService.findByStatus(RapperStatus.CONVICTED).size());
        model.addAttribute("accusedCount",   rapperService.findByStatus(RapperStatus.ACCUSED).size());
        model.addAttribute("accusationCount", accusationService.findAll().size());
        return "home";
    }
}
