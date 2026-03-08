package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.WithdrawalReason;
import org.balancetonrappeur.service.AccusationService;
import org.balancetonrappeur.service.WithdrawalRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/legal")
@RequiredArgsConstructor
public class LegalController {

    private final WithdrawalRequestService withdrawalRequestService;
    private final AccusationService accusationService;

    @GetMapping
    public String legal(
            @RequestParam(required = false) Long accusationId,
            @RequestParam(required = false) Long rapperId,
            @RequestParam(required = false) String rapperName,
            Model model
    ) {
        // Vient d'une fiche accusation → tout locké
        if (accusationId != null) {
            accusationService.findById(accusationId).ifPresent(a -> {
                model.addAttribute("prefillAccusationId", a.getId());
                model.addAttribute("prefillAccusationTitle", a.getTitle());
                model.addAttribute("prefillRapperName", a.getRapper().getName());
            });
        // Vient d'une fiche rappeur → rappeur locké, accusations en dropdown
        } else if (rapperId != null) {
            model.addAttribute("prefillRapperId", rapperId);
            if (rapperName != null) model.addAttribute("prefillRapperName", rapperName);
        // Sans contexte → tout libre
        } else if (rapperName != null) {
            model.addAttribute("prefillRapperName", rapperName);
        }

        model.addAttribute("reasons", WithdrawalReason.values());
        return "legal";
    }

    @PostMapping("/retrait")
    public String submitWithdrawal(
            @RequestParam(required = false) Long accusationId,
            @RequestParam(required = false) String accusationTitle,
            @RequestParam(required = false) String rapperName,
            @RequestParam WithdrawalReason reason,
            @RequestParam String message,
            @RequestParam(required = false) String email,
            RedirectAttributes redirectAttributes
    ) {
        withdrawalRequestService.submit(accusationId, accusationTitle, rapperName, reason, message, email);
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/legal#retrait";
    }
}

