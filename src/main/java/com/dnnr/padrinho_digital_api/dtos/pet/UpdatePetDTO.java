package com.dnnr.padrinho_digital_api.dtos.pet;

import com.dnnr.padrinho_digital_api.entities.pet.PetGender;
import com.dnnr.padrinho_digital_api.entities.pet.PetStatus;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdatePetDTO(
        @Size(min = 1, message = "O nome não pode ser vazio.")
        String name,

        @PastOrPresent(message = "A data de nascimento não pode ser futura.")
        LocalDate birth_date,

        PetStatus status,

        @Size(max = 50, message = "O limite da raça é de 50 caracteres")
        String breed,

        @Positive(message = "O peso deve ser positivo.")
        BigDecimal weight,

        @PastOrPresent(message = "A data de admissão não pode ser futura.")
        LocalDate date_of_admission,

        PetGender gender,

        @Size(min = 20, message = "O perfil deve ter no mínimo 20 caracteres.")
        String profile
) {
}