package com.dnnr.padrinho_digital_api.dtos.report;

import java.math.BigDecimal;

public record ExpenseDistributionDTO(
        String label, // Ex: "Ração"
        BigDecimal value
) {}
