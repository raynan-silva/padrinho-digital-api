package com.dnnr.padrinho_digital_api.dtos.ong;

import java.math.BigDecimal;

public record OngDashboardPetDTO(
        Long id,
        String name,
        String photo, // Base64
        String description, // "Cão ● Vira-lata ● 3 anos"
        String status, // "APADRINHADO" ou "DISPONIVEL"
        long godfatherCount,
        BigDecimal currentMonthlyAmount,
        BigDecimal totalCost
) {}
