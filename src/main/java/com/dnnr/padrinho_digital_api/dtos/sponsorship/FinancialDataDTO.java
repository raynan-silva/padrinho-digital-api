package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import java.math.BigDecimal;
import java.util.List;

public record FinancialDataDTO(
        BigDecimal totalExpenses,     // antigo: totalDespesas
        BigDecimal contributionAmount, // antigo: contribuicao
        String coveragePercentage,    // antigo: cobertura (ex: "33%")
        List<ExpenseItemDTO> details  // antigo: detalhes
) {}
