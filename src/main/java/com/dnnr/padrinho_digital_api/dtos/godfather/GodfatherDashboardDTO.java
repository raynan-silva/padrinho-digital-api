package com.dnnr.padrinho_digital_api.dtos.godfather;

import java.util.List;

public record GodfatherDashboardDTO(
        DashboardStatsDTO stats,
        List<RecentSponsorshipDTO> recentSponsorships
) {}
