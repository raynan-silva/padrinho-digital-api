package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateSponsorshipDTO(
        @NotNull(message = "O ID do pet é obrigatório.")
        Long petId,

        @NotNull(message = "O valor mensal é obrigatório.")
        @Positive(message = "O valor deve ser positivo.")
        BigDecimal monthlyAmount,

        @NotNull(message = "A data de início é obrigatória.")
        @FutureOrPresent(message = "A data de início não pode ser no passado.")
        LocalDate startDate
) {
}