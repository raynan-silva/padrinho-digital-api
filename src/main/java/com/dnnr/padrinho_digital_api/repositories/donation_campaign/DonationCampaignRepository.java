package com.dnnr.padrinho_digital_api.repositories.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.DonationCampaign;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DonationCampaignRepository extends JpaRepository<DonationCampaign, Long> {
    // --- Métodos para o Gerente ---
    Page<DonationCampaign> findAllByOngId (Long ongId, Pageable pageable);

    Optional<DonationCampaign> findByIdAndOngId (Long id, Long ongId);

    // --- Métodos para o Padrinho (Listagem pública) ---
    // (Opcional, mas útil)
    Page<DonationCampaign> findAllByStatus (CampaignStatus status, Pageable pageable);

    // --- Método para o Scheduler (Regra 5 e 6) ---
    @Modifying
    @Query("UPDATE DonationCampaign c SET c.status = 'CONCLUIDA' " +
            "WHERE c.endDate < :today AND c.status = 'ATIVA'")
    void closeExpiredCampaigns (@Param("today") LocalDate today);
}