package com.dnnr.padrinho_digital_api.dtos.report;

import java.math.BigDecimal;

public record SponsorshipReportStatsDTO(
        long activeCount,
        BigDecimal activeRevenue,
        long cancelledCountMonth,
        BigDecimal lostRevenueMonth
) {}
