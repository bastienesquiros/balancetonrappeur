package org.balancetonrappeur.service;

import lombok.RequiredArgsConstructor;
import org.balancetonrappeur.entity.*;
import org.balancetonrappeur.repository.AccusationRepository;
import org.balancetonrappeur.repository.RapperRepository;
import org.balancetonrappeur.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.balancetonrappeur.exception.NoChangeDetectedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final RapperRepository rapperRepository;
    private final AccusationRepository accusationRepository;

    @Transactional
    public void submitUnknownRapper(String rapperName, AccusationCategory category, String title,
                                    AccusationStatus status, LocalDate factDate,
                                    List<SourceType> types, List<String> titles, List<String> urls) {
        var submission = buildBase(category, title, status, factDate);
        submission.setType(SubmissionType.ADD_ACCUSATION);
        submission.setUnknownRapperName(rapperName);
        attachSources(submission, types, titles, urls);
        submissionRepository.save(submission);
    }

    @Transactional
    public void submitAdd(Long rapperId, AccusationCategory category, String title,
                          AccusationStatus status, LocalDate factDate,
                          List<SourceType> types, List<String> titles, List<String> urls) {
        var rapper = rapperRepository.findById(rapperId)
                .orElseThrow(() -> new IllegalArgumentException("Rappeur introuvable"));
        var submission = buildBase(category, title, status, factDate);
        submission.setType(SubmissionType.ADD_ACCUSATION);
        submission.setRapper(rapper);
        attachSources(submission, types, titles, urls);
        submissionRepository.save(submission);
    }

    @Transactional
    public void submitEdit(Long rapperId, Long accusationId, AccusationCategory category,
                           String title, AccusationStatus status, LocalDate factDate,
                           List<SourceType> types, List<String> titles, List<String> urls) {
        var rapper = rapperRepository.findById(rapperId)
                .orElseThrow(() -> new IllegalArgumentException("Rappeur introuvable"));
        var accusation = accusationRepository.findById(accusationId)
                .orElseThrow(() -> new IllegalArgumentException("Accusation introuvable"));

        // Vérifier qu'au moins un champ a changé
        boolean fieldsChanged = accusation.getCategory() != category
                || !Objects.equals(accusation.getTitle(), title)
                || accusation.getStatus() != status
                || !Objects.equals(accusation.getFactDate(), factDate);

        // Vérifier si les sources ont changé (URLs proposées vs existantes)
        var existingUrls = accusation.getSources().stream()
                .map(Source::getUrl).collect(java.util.stream.Collectors.toSet());
        var proposedUrls = urls.stream()
                .filter(u -> u != null && !u.isBlank())
                .collect(java.util.stream.Collectors.toSet());
        boolean sourcesChanged = !existingUrls.equals(proposedUrls);

        if (!fieldsChanged && !sourcesChanged) {
            throw new NoChangeDetectedException();
        }

        var submission = buildBase(category, title, status, factDate);
        submission.setType(SubmissionType.EDIT_ACCUSATION);
        submission.setRapper(rapper);
        submission.setAccusation(accusation);
        attachSources(submission, types, titles, urls);
        submissionRepository.save(submission);
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Submission buildBase(AccusationCategory category, String title,
                                  AccusationStatus status, LocalDate factDate) {
        var s = new Submission();
        s.setCategory(category);
        s.setTitle(title);
        s.setStatus(status);
        s.setFactDate(factDate);
        return s;
    }

    private void attachSources(Submission submission,
                                List<SourceType> types, List<String> titles, List<String> urls) {
        for (int i = 0; i < urls.size(); i++) {
            if (urls.get(i) == null || urls.get(i).isBlank()) continue;
            var src = new SubmissionSource();
            src.setSubmission(submission);
            src.setType(types.size() > i ? types.get(i) : SourceType.OTHER);
            src.setTitle(titles.size() > i ? titles.get(i) : "");
            src.setUrl(urls.get(i));
            submission.getSources().add(src);
        }
    }
}
