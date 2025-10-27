package com.dnnr.padrinho_digital_api.dtos.photo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AddPhotosDTO(
        @NotEmpty(message = "A lista de fotos não pode ser vazia.")
        List<@NotBlank(message = "A foto em Base64 não pode ser nula ou vazia.") String> photos
) {
}
