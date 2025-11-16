package com.dnnr.padrinho_digital_api.dtos.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Address;

public record AddressResponseDTO(Long id, String uf, String city, String number, String complement, String street, String getNeighborhood) {
    public AddressResponseDTO(Address address) {
        this(
                address.getId(),
                address.getUf(),
                address.getCity(),
                address.getNumber(),
                address.getComplement(),
                address.getStreet(),
                address.getNeighborhood()
        );
    }
}
