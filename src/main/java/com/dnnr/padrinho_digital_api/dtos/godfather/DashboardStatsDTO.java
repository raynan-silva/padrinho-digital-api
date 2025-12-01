package com.dnnr.padrinho_digital_api.dtos.godfather;

import java.math.BigDecimal;

public record DashboardStatsDTO(
        long totalSponsoredAnimals,
        BigDecimal totalInvested,
        BigDecimal averageInvestedPerAnimal,
        long campaignParticipations,
        long messagesReceived
) {}
