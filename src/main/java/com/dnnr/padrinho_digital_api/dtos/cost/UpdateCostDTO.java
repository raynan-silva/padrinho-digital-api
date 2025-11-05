package com.dnnr.padrinho_digital_api.dtos.cost;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateCostDTO(
        @NotBlank(message = "O nome da despesa é obrigatório.")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
        String name,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotNull(message = "O valor mensal é obrigatório.")
        @Positive(message = "O valor mensal deve ser positivo.")
        BigDecimal monthlyAmount,

        @NotNull(message = "A data de início do novo período é obrigatória.")
        @PastOrPresent(message = "A data de início não pode ser futura.")
        LocalDate startDate
) {
}