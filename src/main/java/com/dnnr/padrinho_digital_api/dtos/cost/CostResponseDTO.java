package com.dnnr.padrinho_digital_api.dtos.cost;

import java.time.LocalDateTime;
import java.util.List;

public record CostResponseDTO(
        Long id,
        Long petId,
        List<CostHistoryResponseDTO> history,
        LocalDateTime createdAt
) {
}