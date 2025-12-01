package com.dnnr.padrinho_digital_api.dtos.ong;

import java.util.List;

public record OngDashboardDTO(
        OngStatsDTO stats,
        List<OngDashboardPetDTO> recentPets
) {}
