package com.dnnr.padrinho_digital_api.dtos.donation_campaign;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateDonationDTO(
        @NotNull(message = "O valor é obrigatório.")
        @Positive(message = "O valor da doação deve ser positivo.")
        BigDecimal amount,

        @NotNull(message = "O ID da campanha é obrigatório.")
        Long campaignId
) {
}
