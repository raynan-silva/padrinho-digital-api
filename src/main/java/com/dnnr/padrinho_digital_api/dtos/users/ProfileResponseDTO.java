package com.dnnr.padrinho_digital_api.dtos.users;

import com.dnnr.padrinho_digital_api.entities.users.Role;

public record ProfileResponseDTO(Long id, String name, String email, Role role) {
}
