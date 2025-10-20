package com.dnnr.padrinho_digital_api.repositories.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OngRepository extends JpaRepository<Ong, Long> {
    Ong findByCnpj(String cnpj);
}
