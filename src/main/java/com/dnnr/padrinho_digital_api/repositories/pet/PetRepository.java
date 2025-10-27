package com.dnnr.padrinho_digital_api.repositories.pet;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {
    /**
     * Busca um Pet pelo ID e já carrega (FETCH) a sua lista de fotos
     * em uma única consulta (LEFT JOIN) para evitar N+1 queries.
     */
    @Query("SELECT p FROM Pet p LEFT JOIN FETCH p.photos WHERE p.id = :id")
    Optional<Pet> findByIdWithPhotos(@Param("id") Long id);
}
