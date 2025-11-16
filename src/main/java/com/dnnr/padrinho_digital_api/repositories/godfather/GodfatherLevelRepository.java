package com.dnnr.padrinho_digital_api.repositories.godfather;

import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GodfatherLevelRepository extends JpaRepository<GodfatherLevel, Long> {
    Optional<GodfatherLevel> findByOrder(Integer order);
    List<GodfatherLevel> findByOrderGreaterThanOrderByOrderAsc(Integer currentOrder);
}
