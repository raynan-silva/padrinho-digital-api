package com.dnnr.padrinho_digital_api.dtos.users;

import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.Status;

public record RegisterDTO(String login, String password, Role role, String name, Status status) {
}
