package com.dnnr.padrinho_digital_api.repositories.users;

import com.dnnr.padrinho_digital_api.entities.users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
