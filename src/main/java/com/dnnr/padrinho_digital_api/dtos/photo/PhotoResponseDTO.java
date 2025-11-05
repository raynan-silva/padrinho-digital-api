package com.dnnr.padrinho_digital_api.dtos.photo;

import com.dnnr.padrinho_digital_api.entities.photo.Photo;

public record PhotoResponseDTO(
        Long id,
        String base64
) {

    public PhotoResponseDTO(Photo photo) {
        this(photo.getId(), photo.getPhoto());
    }
}
