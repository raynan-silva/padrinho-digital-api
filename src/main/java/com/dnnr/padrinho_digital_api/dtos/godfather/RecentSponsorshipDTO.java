package com.dnnr.padrinho_digital_api.dtos.godfather;

import java.time.LocalDate;

public record RecentSponsorshipDTO(
        Long sponsorshipId,
        Long petId,
        String petName,
        String petPhoto, // Base64 ou URL
        String petSpeciesBreed, // Ex: "Cão ● Vira-lata"
        String petAge, // Ex: "3 anos"
        LocalDate nextDonationDate
) {}
