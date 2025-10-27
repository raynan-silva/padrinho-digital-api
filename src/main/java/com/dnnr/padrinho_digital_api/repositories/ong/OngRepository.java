package com.dnnr.padrinho_digital_api.repositories.ong;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OngRepository extends JpaRepository<Ong, Long> {
    Ong findByCnpj(String cnpj);
    /**
     * Busca uma Ong pelo ID e já carrega (FETCH) a sua lista de fotos
     * em uma única consulta (LEFT JOIN) para evitar N+1 queries.
     */
    @Query("SELECT p FROM Ong p LEFT JOIN FETCH p.photos WHERE p.id = :id")
    Optional<Ong> findByIdWithPhotos(@Param("id") Long id);
}
