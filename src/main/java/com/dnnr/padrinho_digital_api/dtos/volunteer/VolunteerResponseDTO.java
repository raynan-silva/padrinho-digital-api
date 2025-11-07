package com.dnnr.padrinho_digital_api.dtos.volunteer;

import com.dnnr.padrinho_digital_api.entities.users.Role;
import com.dnnr.padrinho_digital_api.entities.users.Volunteer;

public record VolunteerResponseDTO(Long id, String name, String email, Role role, String photo, Long ong_id) {
    public VolunteerResponseDTO(Volunteer volunteer) {
        this(
                volunteer.getId(),
                volunteer.getUser().getName(),
                volunteer.getUser().getEmail(),
                volunteer.getUser().getRole(),
                volunteer.getUser().getPhoto(),
                volunteer.getOng().getId()
        );
    }
}
