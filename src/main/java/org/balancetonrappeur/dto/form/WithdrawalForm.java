package org.balancetonrappeur.dto.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.balancetonrappeur.entity.WithdrawalReason;

@Data
public class WithdrawalForm {

    private Long rapperId;

    @NotNull(message = "Veuillez sélectionner une affaire concernée.")
    private Long accusationId;
    private String accusationTitle;

    @NotBlank(message = "Le nom du rappeur est obligatoire.")
    private String rapperName;

    @NotNull(message = "Veuillez sélectionner un motif.")
    private WithdrawalReason reason;

    @NotBlank(message = "Le champ détails est obligatoire.")
    private String message;

    private String email;

}

