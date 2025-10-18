package com.dnnr.padrinho_digital_api.repository.users;

import com.dnnr.padrinho_digital_api.entity.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
