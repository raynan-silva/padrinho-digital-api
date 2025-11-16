package com.dnnr.padrinho_digital_api.dtos.godfather;

public record GodfatherProfileStatsDTO(
        long activeSponsorshipsCount,
        long conqueredSealsCount,
        String levelName,
        String levelIcon
) {
}
