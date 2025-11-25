package com.dnnr.padrinho_digital_api.dtos.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;

import java.time.LocalDate;

public record  OngsPendingResponseDTO (
        Long id,
        String photo,
        String name,
        String city,
        String state,
        LocalDate registrationDate,
        OngStatus status
) {
    public OngsPendingResponseDTO(Ong ong) {
        this(
                ong.getId(),
                (ong.getPhotos() != null && !ong.getPhotos().isEmpty())
                        ? ong.getPhotos().get(0).getPhoto()
                        : null,
                ong.getName(),
                ong.getAddress().getCity(),
                ong.getAddress().getUf(),
                ong.getRegistrationDate(),
                ong.getStatus()
        );
    }
}
