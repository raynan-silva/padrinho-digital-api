package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.entities.pet.CostHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostHistoryRepository extends JpaRepository<CostHistory, Long> {
}
