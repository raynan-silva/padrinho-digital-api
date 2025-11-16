package com.dnnr.padrinho_digital_api.repositories.sponsorship;

import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SponsorshipHistoryRepository extends JpaRepository<SponsorshipHistory, Long> {

    // Encontra o período ATUAL (onde a data fim é nula)
    @Query("SELECT h FROM SponsorshipHistory h WHERE h.sponsorship.id = :sponsorshipId AND h.endDate IS NULL")
    Optional<SponsorshipHistory> findCurrentHistoryBySponsorshipId(@Param("sponsorshipId") Long sponsorshipId);
    long countBySponsorshipGodfatherIdAndStatus(Long godfatherId, SponsorshipStatus status);
}