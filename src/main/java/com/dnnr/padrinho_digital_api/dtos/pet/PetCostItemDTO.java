package com.dnnr.padrinho_digital_api.dtos.pet;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PetCostItemDTO(
        @NotBlank(message = "O nome da despesa é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String name,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotNull(message = "O valor mensal é obrigatório.")
        @Positive(message = "O valor mensal deve ser positivo.")
        BigDecimal monthlyAmount,

        @NotNull(message = "A data de início é obrigatória.")
        @PastOrPresent(message = "A data de início não pode ser futura.")
        LocalDate startDate
) {
}