package com.dnnr.padrinho_digital_api.dtos.users;

import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.br.CNPJ;

public record RegisterOngDTO(
        @NotBlank(message = "O login (email) é obrigatório.")
        @Email(message = "O formato do email é inválido.")
        String login,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, max = 30, message = "A senha deve ter entre 6 e 30 caracteres.")
        String password,

        @NotBlank(message = "O nome é obrigatório.")
        String name,

        @NotBlank(message = "O nome da ONG é obrigatório.")
        String ong_name,

        @NotBlank(message = "O CNPJ é obrigatório.")
        @CNPJ(message = "O CNPJ informado é inválido.")
        String cnpj,

        @NotBlank(message = "O telefone é obrigatório.")
        String phone,

        @NotBlank(message = "A descrição é obrigatória.")
        String description,

        @NotBlank(message = "A rua é obrigatória.")
        String street,

        @NotBlank(message = "O número do endereço é obrigatório.")
        String address_number,

        @NotBlank(message = "O bairro é obrigatório.")
        String neighborhood,

        @NotBlank(message = "A cidade é obrigatória.")
        String city,

        @NotBlank(message = "A UF é obrigatória.")
        @Size(min = 2, max = 2, message = "A UF deve ter 2 caracteres.")
        String uf,

        String complement, // Complemento pode ser nulo/em branco

        @NotBlank(message = "O CEP é obrigatório.")
        @Size(min = 8, max = 8, message = "O CEP deve ter 8 caracteres.")
        String cep
) {
}
