package com.dnnr.padrinho_digital_api.dtos.cost;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CostHistoryResponseDTO(
        Long id,
        BigDecimal monthlyAmount,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        String name,
        LocalDateTime createdAt
) {
}