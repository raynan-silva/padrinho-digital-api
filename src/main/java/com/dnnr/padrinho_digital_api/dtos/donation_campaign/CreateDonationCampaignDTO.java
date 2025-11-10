package com.dnnr.padrinho_digital_api.dtos.donation_campaign;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateDonationCampaignDTO(
        @NotBlank(message = "O título é obrigatório.")
        @Size(max = 300)
        String title,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotNull(message = "A data de início é obrigatória.")
        @FutureOrPresent(message = "A data de início não pode ser no passado.")
        LocalDate startDate,

        @Future(message = "A data final deve ser no futuro.")
        LocalDate endDate, // Pode ser nulo

        @NotNull(message = "O valor alvo é obrigatório.")
        @Positive(message = "O valor alvo deve ser positivo.")
        BigDecimal valueTarget,

        @NotBlank(message = "A foto é obrigatória. Em string base64.")
        String photo
) {
}
