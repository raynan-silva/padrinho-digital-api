package com.dnnr.padrinho_digital_api.services.mappers;

import com.dnnr.padrinho_digital_api.dtos.cost.CostHistoryResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.cost.CostResponseDTO;
import com.dnnr.padrinho_digital_api.entities.pet.Cost;
import com.dnnr.padrinho_digital_api.entities.pet.CostHistory;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class CostMapper {

    public CostHistoryResponseDTO toHistoryDTO(CostHistory history) {
        return new CostHistoryResponseDTO(
                history.getId(),
                history.getMonthlyAmount(),
                history.getStartDate(),
                history.getEndDate(),
                history.getDescription(),
                history.getName(),
                history.getCreatedAt()
        );
    }

    public CostResponseDTO toCostDTO(Cost cost) {
        return new CostResponseDTO(
                cost.getId(),
                cost.getPet().getId(),
                cost.getHistory().stream()
                        .map(this::toHistoryDTO)
                        .collect(Collectors.toList()),
                cost.getCreatedAt()
        );
    }
}