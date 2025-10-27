package com.dnnr.padrinho_digital_api.dtos.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.pet.PetGender;
import com.dnnr.padrinho_digital_api.entities.pet.PetStatus;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record PetResponseDTO(
        Long id,
        String name,
        LocalDate birth_date,
        PetStatus status,
        String breed,
        BigDecimal weight,
        LocalDate date_of_admission,
        PetGender gender,
        String profile,
        Long ongId,
        String ongName,
        List<String> photos
) {
    // Construtor de conveniência para mapear da Entidade Pet
    public PetResponseDTO(Pet pet) {
        this(
                pet.getId(),
                pet.getName(),
                pet.getBirthDate(),
                pet.getStatus(),
                pet.getBreed(),
                pet.getWeight(),
                pet.getDateOfAdmission(),
                pet.getGender(),
                pet.getProfile(),
                pet.getOng().getId(),
                pet.getOng().getName(),
                pet.getPhotos() != null ?
                        pet.getPhotos().stream().map(Photo::getPhoto).toList() :
                        Collections.emptyList()
        );
    }
}