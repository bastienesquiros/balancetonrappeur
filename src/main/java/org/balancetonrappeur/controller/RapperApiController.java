package org.balancetonrappeur.controller;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.dto.AccusationResultDto;
import org.balancetonrappeur.dto.RapperSearchResultDto;
import org.balancetonrappeur.service.AccusationService;
import org.balancetonrappeur.service.RapperService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rappers")
@RequiredArgsConstructor
public class RapperApiController {

    private final RapperService rapperService;
    private final AccusationService accusationService;

    @GetMapping("/search")
    public List<RapperSearchResultDto> search(@RequestParam String q) {
        return rapperService.searchForAutocomplete(q).stream()
                .map(r -> new RapperSearchResultDto(r.getId(), r.getName(), r.getStatus().label(), r.getSpotifyImageUrl()))
                .toList();
    }

    @GetMapping("/{id}/accusations")
    public List<AccusationResultDto> accusations(@PathVariable Long id) {
        return accusationService.findByRapper(id).stream()
                .map(a -> new AccusationResultDto(a.getId(), a.getTitle()))
                .toList();
    }
}
