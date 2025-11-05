package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Cost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostRepository extends JpaRepository<Cost, Long> {
    List<Cost> findAllByPetId(Long petId);
}
