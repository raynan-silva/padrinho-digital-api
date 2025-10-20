package com.dnnr.padrinho_digital_api.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(
        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "Formato de email inválido.")
        String email
) {
}
