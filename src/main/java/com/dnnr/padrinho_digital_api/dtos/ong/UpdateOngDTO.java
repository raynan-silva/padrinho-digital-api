package com.dnnr.padrinho_digital_api.dtos.ong;

import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateOngDTO(
        String photo_manager,

        String manager_name,

        List<String> photos_ong,

        @Size(min = 1, message = "O nome não pode ser vazio")
        String name,

        String phone,

        @Size(min = 20, message = "A descrição deve no mínimo 20 caracteres")
        String description,

        @Size(min = 8, max = 8, message = "O CEP deve ter 8 caracteres.")
        String cep,

        String street,

        String address_number,

        String neighborhood,

        String city,

        @Size(min = 2, max = 2, message = "A UF deve ter 2 caracteres.")
        String uf,

        String complement
) {
}
