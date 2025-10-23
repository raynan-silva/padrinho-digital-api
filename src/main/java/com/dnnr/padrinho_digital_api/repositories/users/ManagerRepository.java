package com.dnnr.padrinho_digital_api.repositories.users;

import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUser(User user);
}
