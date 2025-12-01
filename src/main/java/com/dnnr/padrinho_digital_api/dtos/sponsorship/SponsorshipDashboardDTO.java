package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import java.util.List;

public record SponsorshipDashboardDTO(
        AnimalDataDTO animalData,
        FinancialDataDTO financialData,
        List<GalleryItemDTO> gallery
) {}