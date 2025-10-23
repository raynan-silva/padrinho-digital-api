package com.dnnr.padrinho_digital_api.repositories.users;

import com.dnnr.padrinho_digital_api.entities.users.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
}
