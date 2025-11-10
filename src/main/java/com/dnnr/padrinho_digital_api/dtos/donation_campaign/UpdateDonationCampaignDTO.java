package com.dnnr.padrinho_digital_api.dtos.donation_campaign;

import com.dnnr.padrinho_digital_api.entities.donation_campaign.CampaignStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdateDonationCampaignDTO(
        @Size(max = 300)
        String title,

        String description,

        @Future(message = "A data final deve ser no futuro.")
        LocalDate endDate,

        @NotNull(message = "O status é obrigatório (ATIVA, PAUSADA, CANCELADA).")
        CampaignStatus status,

        String photo
) {
    // Construtor para garantir que status perigosos não sejam passados
    public UpdateDonationCampaignDTO {
        if (status == CampaignStatus.CONCLUIDA) {
            throw new IllegalArgumentException("O status não pode ser alterado para CONCLUIDA manualmente.");
        }
    }
}