package com.dnnr.padrinho_digital_api.repositories.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
