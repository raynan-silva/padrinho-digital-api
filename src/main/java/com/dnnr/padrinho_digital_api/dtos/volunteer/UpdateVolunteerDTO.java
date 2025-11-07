package com.dnnr.padrinho_digital_api.dtos.volunteer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateVolunteerDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(max = 200, message = "O nome deve ter no máximo 200 caracteres.")
        String name,

        String photo
) {
}
