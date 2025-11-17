package com.dnnr.padrinho_digital_api.repositories.users;

import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findByUser(User user);

    @Query("SELECT m FROM Manager m JOIN FETCH m.user u WHERE m.ong.id = :ongId")
    Optional<Manager> findManagerWithUserByOngId(@Param("ongId") Long ongId);
}
