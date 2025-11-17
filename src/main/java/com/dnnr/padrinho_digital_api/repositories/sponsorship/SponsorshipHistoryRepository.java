package com.dnnr.padrinho_digital_api.repositories.sponsorship;

import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SponsorshipHistoryRepository extends JpaRepository<SponsorshipHistory, Long> {

    // Encontra o período ATUAL (onde a data fim é nula)
    @Query("SELECT h FROM SponsorshipHistory h WHERE h.sponsorship.id = :sponsorshipId AND h.endDate IS NULL")
    Optional<SponsorshipHistory> findCurrentHistoryBySponsorshipId(@Param("sponsorshipId") Long sponsorshipId);
    long countBySponsorshipGodfatherIdAndStatus(Long godfatherId, SponsorshipStatus status);

    // Para Padrinho: Busca históricos ativos
    Page<SponsorshipHistory> findBySponsorshipGodfatherAndEndDateIsNull(Godfather godfather, Pageable pageable);

    // Para Gerente/Voluntário: Busca históricos ativos da ONG
    Page<SponsorshipHistory> findBySponsorshipPetOngAndEndDateIsNull(Ong ong, Pageable pageable);

    // Para Admin (por Pet): Busca históricos ativos de um pet
    Page<SponsorshipHistory> findBySponsorshipPetAndEndDateIsNull(Pet pet, Pageable pageable);

    @Query("SELECT sh FROM SponsorshipHistory sh " +
            "JOIN FETCH sh.sponsorship s " +
            "JOIN FETCH s.pet p " +
            "JOIN FETCH p.ong o " +
            "WHERE s.godfather.id = :godfatherId AND sh.endDate IS NULL")
    List<SponsorshipHistory> findAllActiveSponsorshipsByGodfatherId(@Param("godfatherId") Long godfatherId);
}