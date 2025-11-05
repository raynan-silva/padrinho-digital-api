package com.dnnr.padrinho_digital_api.repositories.sponsorship;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SponsorshipRepository extends JpaRepository<Sponsorship, Long> {

    // Usado para verificar duplicidade
    Optional<Sponsorship> findByGodfatherAndPet(Godfather godfather, Pet pet);

    // Para listagem do Padrinho
    Page<Sponsorship> findByGodfather(Godfather godfather, Pageable pageable);

    // Para listagem da ONG
    Page<Sponsorship> findByPet_Ong(Ong ong, Pageable pageable);

    // Para listagem do Admin por Pet
    Page<Sponsorship> findByPet(Pet pet, Pageable pageable);

    // Para GET by ID (trazendo todo o histórico)
    @Query("SELECT s FROM Sponsorship s LEFT JOIN FETCH s.history WHERE s.id = :id")
    Optional<Sponsorship> findByIdWithHistory(@Param("id") Long id);
}