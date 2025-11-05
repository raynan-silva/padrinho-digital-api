package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateSponsorshipDTO(
        @NotNull(message = "O novo status é obrigatório.")
        SponsorshipStatus newStatus,

        @Positive(message = "O valor deve ser positivo.")
        BigDecimal newAmount,

        String notes
) {
}