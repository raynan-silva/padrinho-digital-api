package com.dnnr.padrinho_digital_api.dtos.ong;

import jakarta.validation.constraints.Size;

public record UpdateOngDTO(
        @Size(min = 1, message = "O nome não pode ser vazio")
        String name,

        String phone,

        @Size(min = 20, message = "A descrição deve no mínimo 20 caracteres")
        String description,

        String street,

        String address_number,

        String neighborhood,

        String city,

        @Size(min = 2, max = 2, message = "A UF deve ter 2 caracteres.")
        String uf,

        String complement, // Complemento pode ser nulo/em branco

        @Size(min = 8, max = 8, message = "O CEP deve ter 8 caracteres.")
        String cep

) {
}
