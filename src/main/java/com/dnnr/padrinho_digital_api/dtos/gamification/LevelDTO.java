package com.dnnr.padrinho_digital_api.dtos.gamification;

import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import java.math.BigDecimal;

public record LevelDTO(
        Long id,
        String name,
        String iconUrl,
        int order,
        BigDecimal requiredAmount // Meta para alcançar
) {
    public LevelDTO(GodfatherLevel entity) {
        this(
                entity.getId(),
                entity.getName(),
                entity.getIconUrl(),
                entity.getOrder(),
                entity.getRequiredAmount()
        );
    }
}