package org.balancetonrappeur.dto.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.balancetonrappeur.entity.AccusationCategory;
import org.balancetonrappeur.entity.AccusationStatus;
import org.balancetonrappeur.entity.SourceType;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
public class SuggestForm {

    @NotBlank(message = "Le nom du rappeur est obligatoire.")
    private String rapperName;

    @NotNull(message = "La catégorie est obligatoire.")
    private AccusationCategory category;

    @NotBlank(message = "Le titre est obligatoire.")
    private String title;

    // Optionnel : pas toutes les affaires ne font l'objet d'une procédure judiciaire
    private AccusationStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate factDate;

    private List<SourceType> sourceType;
    private List<String> sourceTitle;
    private List<String> sourceUrl;

    @Email(message = "L'adresse email n'est pas valide.")
    private String email;
}
