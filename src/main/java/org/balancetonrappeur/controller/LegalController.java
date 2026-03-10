package org.balancetonrappeur.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.dto.form.WithdrawalForm;
import org.balancetonrappeur.entity.WithdrawalReason;
import org.balancetonrappeur.service.AccusationService;
import org.balancetonrappeur.service.WithdrawalRequestService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        var form = new WithdrawalForm();

        if (accusationId != null) {
            accusationService.findById(accusationId).ifPresent(a -> {
                form.setAccusationId(a.getId());
                form.setAccusationTitle(a.getTitle());
                form.setRapperName(a.getRapper().getName());
            });
        } else if (rapperId != null) {
            form.setRapperId(rapperId);
            if (rapperName != null) {
                form.setRapperName(rapperName);
                model.addAttribute("prefillRapperName", rapperName);
            }
            model.addAttribute("prefillRapperId", rapperId);
        } else if (rapperName != null) {
            form.setRapperName(rapperName);
        }

        model.addAttribute("withdrawalForm", form);
        model.addAttribute("reasons", WithdrawalReason.values());
        return "legal";
    }

    @PostMapping("/retrait")
    public String submitWithdrawal(
            @Valid @ModelAttribute("withdrawalForm") WithdrawalForm form,
            BindingResult binding,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (binding.hasErrors()) {
            model.addAttribute("reasons", WithdrawalReason.values());
            model.addAttribute("formHasErrors", true);
            if (form.getRapperId() != null) {
                model.addAttribute("prefillRapperId", form.getRapperId());
            }
            return "legal";
        }

        withdrawalRequestService.submit(form.getAccusationId(), form.getAccusationTitle(),
                form.getRapperName(), form.getReason(), form.getMessage(), form.getEmail());
        redirectAttributes.addFlashAttribute("success", true);
        return "redirect:/legal#retrait";
    }
}
