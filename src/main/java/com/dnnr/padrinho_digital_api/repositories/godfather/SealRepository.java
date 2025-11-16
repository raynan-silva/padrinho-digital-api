package com.dnnr.padrinho_digital_api.repositories.godfather;

import com.dnnr.padrinho_digital_api.entities.godfather.Seal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SealRepository extends JpaRepository<Seal, Long> {
    Optional<Seal> findByTriggerMetric(String triggerMetric);
}
