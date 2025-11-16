package com.dnnr.padrinho_digital_api.dtos.users;

import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.User;

public record ProfileResponseDTO(Long id, String name, String email, Role role, String photo) {
    public ProfileResponseDTO(User user) {
        this(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getPhoto()
        );
    }
}
