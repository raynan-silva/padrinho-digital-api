package com.dnnr.padrinho_digital_api.repositories.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    long countByGodfatherId(Long godfatherId);

    @Query("SELECT COALESCE(SUM(d.amount), 0) FROM Donation d WHERE d.godfather.id = :godfatherId")
    Optional<BigDecimal> sumAmountByGodfatherId(@Param("godfatherId") Long godfatherId);
}
