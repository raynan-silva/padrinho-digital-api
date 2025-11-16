package com.dnnr.padrinho_digital_api.dtos.gamification;

import com.dnnr.padrinho_digital_api.entities.godfather.Seal;
import java.math.BigDecimal;

public record SealDTO(
        Long id,
        String name,
        String description,
        String iconUrl,
        String triggerMetric, // "DONATION_COUNT", etc.
        BigDecimal triggerValue // A meta (ex: 5, 100.00)
) {
    public SealDTO(Seal entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getIconUrl(),
                entity.getTriggerMetric(),
                entity.getTriggerValue()
        );
    }
}