package org.balancetonrappeur.controller;

import org.balancetonrappeur.service.AccusationService;
import org.balancetonrappeur.service.RapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rappers")
@RequiredArgsConstructor
public class RapperApiController {

    private final RapperService rapperService;
    private final AccusationService accusationService;

    record RapperSearchResult(Long id, String name, String status, String imageUrl) {}
    record AccusationResult(Long id, String title) {}

    @GetMapping("/search")
    public List<RapperSearchResult> search(@RequestParam String q) {
        return rapperService.searchForAutocomplete(q).stream()
                .map(r -> new RapperSearchResult(r.getId(), r.getName(), statusLabel(r.getStatus()), r.getSpotifyImageUrl()))
                .toList();
    }

    private String statusLabel(org.balancetonrappeur.entity.RapperStatus status) {
        return switch (status) {
            case CONVICTED  -> "⛔ Condamné";
            case ACCUSED    -> "🚨 Accusé";
            default         -> "⚠️ Polémique";
        };
    }

    @GetMapping("/{id}/accusations")
    public List<AccusationResult> accusations(@PathVariable Long id) {
        return accusationService.findByRapper(id).stream()
                .map(a -> new AccusationResult(a.getId(), a.getTitle()))
                .toList();
    }
}
