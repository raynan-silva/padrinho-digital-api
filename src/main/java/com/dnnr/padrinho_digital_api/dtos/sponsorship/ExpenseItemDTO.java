package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseItemDTO(
        String category,    // antigo: categoria
        LocalDate date,     // antigo: data
        String description, // antigo: descricao
        BigDecimal amount   // antigo: valor
) {}
