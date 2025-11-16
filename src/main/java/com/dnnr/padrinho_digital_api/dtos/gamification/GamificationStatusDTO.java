package com.dnnr.padrinho_digital_api.dtos.gamification;

import java.util.List;

public record GamificationStatusDTO(
        LevelDTO currentLevel,
        List<LevelDTO> nextLevels,
        List<SealDTO> conqueredSeals,
        List<SealDTO> unconqueredSeals
) {
}