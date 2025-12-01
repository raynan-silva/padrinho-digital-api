package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import java.time.LocalDate;

public record GalleryItemDTO(
        Long id,
        LocalDate date,
        String imageUrl // Base64
) {}
