package com.dnnr.padrinho_digital_api.dtos.pet;

import com.dnnr.padrinho_digital_api.entities.pet.PetGender;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePetDTO(
        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @PastOrPresent(message = "A data de nascimento não pode ser uma data futura.")
        LocalDate birth_date,

        @NotBlank(message = "A raça não pode ser nula.")
        @Size(max = 50, message = "O limite é de 50 caracteres")
        String breed,

        @NotNull(message = "O peso é obrigatório.")
        @Positive(message = "O peso deve ser um valor positivo.")
        BigDecimal weight,

        @NotNull(message = "A data de admissão é obrigatória.")
        @PastOrPresent(message = "A data de admissão não pode ser uma data futura.")
        LocalDate date_of_admission,

        @NotNull(message = "O gênero é obrigatório.")
        PetGender gender,

        @NotBlank(message = "O perfil é obrigatório.")
        @Size(min = 20, message = "O perfil deve ter no mínimo 20 caracteres.")
        String profile
) {
}
