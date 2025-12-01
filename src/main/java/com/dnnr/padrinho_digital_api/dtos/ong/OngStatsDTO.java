package com.dnnr.padrinho_digital_api.dtos.ong;

import java.math.BigDecimal;

public record OngStatsDTO(
        // Card 1: Animais
        long totalAnimals,
        long totalSponsored,
        long totalAvailable,
        long newAnimalsThisWeek,

        // Card 2: Padrinhos
        long activeGodfathers,
        double godfathersGrowthPct, // Ex: 12.0

        // Card 3: Receita
        BigDecimal monthlyRevenue,
        BigDecimal revenueGoal,
        double revenueGrowthPct,

        // Card 4: Mensagens
        long pendingMessages
) {}
