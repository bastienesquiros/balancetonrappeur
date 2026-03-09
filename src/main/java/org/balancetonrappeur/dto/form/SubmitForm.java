package org.balancetonrappeur.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.balancetonrappeur.entity.SourceType;
import org.balancetonrappeur.entity.SubmissionType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class SubmitForm {

    private SubmissionType type;
    private Long accusationId;

    @NotNull(message = "La catégorie est obligatoire.")
    private AccusationCategory category;

    @NotBlank(message = "Le titre est obligatoire.")
    private String title;

    @NotNull(message = "Le statut juridique est obligatoire.")
    private AccusationStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate factDate;

    // Validation manuelle dans le controller (liste dynamique)
    private List<SourceType> sourceType;
    private List<String> sourceTitle;
    private List<String> sourceUrl;

}

