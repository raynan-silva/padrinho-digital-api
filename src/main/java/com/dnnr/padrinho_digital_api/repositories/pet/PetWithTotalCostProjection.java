package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;

import java.math.BigDecimal;

public interface PetWithTotalCostProjection {
    Pet getPet();
    BigDecimal getTotalCost();
}
