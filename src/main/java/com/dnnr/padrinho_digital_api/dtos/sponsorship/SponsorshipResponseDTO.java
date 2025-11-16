package com.dnnr.padrinho_digital_api.dtos.sponsorship;

import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Este DTO é a resposta para GET /sponsorships/{id}
public record SponsorshipResponseDTO(
        Long sponsorshipId,
        PetInfoDTO pet,
        GodfatherInfoDTO godfather,
        OngInfoDTO ong,
        SponsorshipStatus currentStatus,
        BigDecimal currentAmount,
        LocalDate currentStartDate
        //List<HistoryItemDTO> history
) {
    public SponsorshipResponseDTO(Sponsorship s, SponsorshipHistory current) {
        this(
                s.getId(),
                new PetInfoDTO(s.getPet()),
                new GodfatherInfoDTO(s.getGodfather()),
                new OngInfoDTO(s.getPet().getOng()), // Pega a ONG através do Pet
                current.getStatus(),
                current.getMonthlyAmount(),
                current.getStartDate()
//                s.getHistory().stream() // Mapeia o histórico completo
//                        .map(HistoryItemDTO::new)
//                        .collect(Collectors.toList())
        );
    }
}

// --- Sub-DTOs ---

record PetInfoDTO(Long id, String name, String photoUrl, LocalDate birthDate, String profile) {
    public PetInfoDTO(Pet pet) {
        this(
                pet.getId(),
                pet.getName(),
                pet.getPhotos() != null && !pet.getPhotos().isEmpty() ?
                        pet.getPhotos().getFirst().getPhoto() : null, // Pega a primeira foto
                pet.getBirthDate(),
                pet.getProfile()
        );
    }
}

record GodfatherInfoDTO(Long id, String name, String email) {
    public GodfatherInfoDTO(com.dnnr.padrinho_digital_api.entities.users.Godfather godfather) {
        this(
                godfather.getId(),
                godfather.getUser().getName(),
                godfather.getUser().getEmail()
        );
    }
}

record OngInfoDTO(Long id, String name) {
    public OngInfoDTO(com.dnnr.padrinho_digital_api.entities.ong.Ong ong) {
        this(ong.getId(), ong.getName());
    }
}

record HistoryItemDTO(
        Long id,
        SponsorshipStatus status,
        BigDecimal monthlyAmount,
        LocalDate startDate,
        LocalDate endDate,
        String notes
) {
    public HistoryItemDTO(SponsorshipHistory h) {
        this(
                h.getId(),
                h.getStatus(),
                h.getMonthlyAmount(),
                h.getStartDate(),
                h.getEndDate(),
                h.getNotes()
        );
    }
}