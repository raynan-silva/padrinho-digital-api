package com.dnnr.padrinho_digital_api.dtos.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public record OngResponseDTO(
        Long id,
        String name,
        String cnpj,
        String phone,
        LocalDate registrationDate,
        String description,
        OngStatus status,
        Address address,
        List<String> photos
) {
    // Construtor de conveniência para mapear da Entidade Ong
    public OngResponseDTO(Ong ong) {
        this(
                ong.getId(),
                ong.getName(),
                ong.getCnpj(),
                ong.getPhone(),
                ong.getRegistrationDate(),
                ong.getDescription(),
                ong.getStatus(),
                ong.getAddress(),
                ong.getPhotos() != null ?
                        ong.getPhotos().stream().map(Photo::getPhoto).toList() :
                        Collections.emptyList()
        );
    }
}
