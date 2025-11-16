package com.dnnr.padrinho_digital_api.repositories.godfather;

import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherSeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GodfatherSealRepository extends JpaRepository<GodfatherSeal, Long> {
    long countByGodfatherId(Long godfatherId);
    List<GodfatherSeal> findAllByGodfatherId(Long godfatherId);
    boolean existsByGodfatherIdAndSealId(Long godfatherId, Long sealId);

    @Query("SELECT gs FROM GodfatherSeal gs JOIN FETCH gs.seal WHERE gs.godfather.id = :godfatherId")
    List<GodfatherSeal> findAllByGodfatherIdWithSeal(@Param("godfatherId") Long godfatherId);
}
