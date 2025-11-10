package com.dnnr.padrinho_digital_api.controllers.donation_compaign;

import com.dnnr.padrinho_digital_api.dtos.donation_campaign.CreateDonationDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.DonationResponseDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.donation_campaign.DonationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService service;

    @PostMapping
    @PreAuthorize("hasRole('PADRINHO')") // Apenas Padrinhos podem doar
    public ResponseEntity<DonationResponseDTO> makeDonation(
            @Valid @RequestBody CreateDonationDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {

        DonationResponseDTO responseDto = service.createDonation(dto, authenticatedUser);
        return ResponseEntity.ok(responseDto);
    }
}
