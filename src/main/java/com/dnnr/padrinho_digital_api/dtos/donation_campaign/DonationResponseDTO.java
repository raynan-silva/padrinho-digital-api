package com.dnnr.padrinho_digital_api.dtos.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.Donation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DonationResponseDTO(
        Long id,
        BigDecimal amount,
        LocalDate date,
        Long godfatherId,
        Long campaignId,
        LocalDateTime createdAt
) {
    public DonationResponseDTO(Donation donation) {
        this(
                donation.getId(),
                donation.getAmount(),
                donation.getDate(),
                donation.getGodfather().getId(),
                donation.getDonationCampaign().getId(),
                donation.getCreatedAt()
        );
    }
}
