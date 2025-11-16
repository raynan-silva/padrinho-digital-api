package com.dnnr.padrinho_digital_api.dtos.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import com.dnnr.padrinho_digital_api.entities.users.Manager;

import java.util.List;

public record OngProfileDTO(Long id, String photo_manager, String manager_name, List<Photo> photos_ong, String name, String phone, String description, String cnpj, String cep, String street,
                            String address_number, String neighborhood, String city, String state, String complement) {

    public OngProfileDTO(Manager manager, Ong ong, Address address){
        this(
                ong.getId(),
                manager.getUser().getPhoto(),
                manager.getUser().getName(),
                ong.getPhotos(),
                ong.getName(),
                ong.getPhone(),
                ong.getDescription(),
                ong.getCnpj(),
                address.getCep(),
                address.getStreet(),
                address.getNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getUf(),
                address.getComplement()
        );
    }
}
