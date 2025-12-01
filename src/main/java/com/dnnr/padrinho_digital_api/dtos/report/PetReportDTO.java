package com.dnnr.padrinho_digital_api.dtos.report;

import java.math.BigDecimal;

public record PetReportDTO(
        Long id,
        String name,
        String status, // "Apadrinhável", "Pausado", etc.
        BigDecimal monthlyGoal, // Custo total
        BigDecimal collected,   // Total apadrinhado
        long activeSponsors
) {}
