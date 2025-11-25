package com.dnnr.padrinho_digital_api.dtos.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PetPublicResponseDTO(
        Long id,
        String name,
        String breed,
        String ongName,
        LocalDate birth_date,
        List<Photo> photos,
        BigDecimal totalCost
) {

    public PetPublicResponseDTO(Pet pet, BigDecimal totalCost) {
        this(
                pet.getId(),
                pet.getName(),
                pet.getBreed(),
                pet.getOng().getName(),
                pet.getBirthDate(),
                pet.getPhotos(),
                totalCost
        );

    }
}
