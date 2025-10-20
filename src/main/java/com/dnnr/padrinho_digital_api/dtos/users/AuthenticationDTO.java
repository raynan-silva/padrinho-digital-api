package com.dnnr.padrinho_digital_api.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @NotBlank(message = "O login (email) é obrigatório.")
        @Email(message = "O formato do email é inválido.")
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 30, message = "A senha deve ter entre 6 e 30 caracteres.")
        String password
) {
}
