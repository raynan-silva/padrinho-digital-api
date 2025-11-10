package com.dnnr.padrinho_digital_api.dtos.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.DonationCampaign;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.CampaignStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DonationCampaignResponseDTO(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal valueTarget,
        BigDecimal amountCollected,
        CampaignStatus status,
        String photo,
        Long ongId
) {
    public DonationCampaignResponseDTO(DonationCampaign campaign) {
        this(
                campaign.getId(),
                campaign.getTitle(),
                campaign.getDescription(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getValueTarget(),
                campaign.getAmountCollected(),
                campaign.getStatus(),
                campaign.getPhoto(),
                campaign.getOng().getId()
        );
    }
}
