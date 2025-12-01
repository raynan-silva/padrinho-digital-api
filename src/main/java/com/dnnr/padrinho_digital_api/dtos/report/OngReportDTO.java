package com.dnnr.padrinho_digital_api.dtos.report;

import java.math.BigDecimal;
import java.util.List;

public record OngReportDTO(
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        List<PetReportDTO> animals,
        List<ExpenseDistributionDTO> expenseDistribution,
        SponsorshipReportStatsDTO sponsorshipStats
) {}
