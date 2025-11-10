package com.dnnr.padrinho_digital_api.controllers.donation_compaign;

import com.dnnr.padrinho_digital_api.dtos.donation_campaign.CreateDonationCampaignDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.DonationCampaignResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.UpdateDonationCampaignDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.donation_campaign.DonationCampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("campaigns")
@RequiredArgsConstructor
public class DonationCampaignController {

    private final DonationCampaignService service;

    // Endpoint PÚBLICO para listar campanhas ativas
    @GetMapping("/active")
    public ResponseEntity<Page<DonationCampaignResponseDTO>> getAllActiveCampaigns(
            @PageableDefault(size = 10, sort = "endDate") Pageable pageable) {
        Page<DonationCampaignResponseDTO> page = service.findAllActive(pageable);
        return ResponseEntity.ok(page);
    }

    // --- Endpoints do GERENTE ---

    @GetMapping("/my-ong")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Page<DonationCampaignResponseDTO>> getAllCampaignsByOng(
            @AuthenticationPrincipal User authenticatedUser,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<DonationCampaignResponseDTO> page = service.findAllByOng(authenticatedUser, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<DonationCampaignResponseDTO> getCampaignById(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {
        DonationCampaignResponseDTO dto = service.findById(id, authenticatedUser);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("{id}/godfather")
    public ResponseEntity<DonationCampaignResponseDTO> getCampaignByIdGodfather(
            @PathVariable Long id) {
        DonationCampaignResponseDTO dto = service.findByIdGodfather(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<DonationCampaignResponseDTO> createCampaign(
            @Valid @RequestBody CreateDonationCampaignDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {
        DonationCampaignResponseDTO newDto = service.create(dto, authenticatedUser);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<DonationCampaignResponseDTO> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDonationCampaignDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {
        DonationCampaignResponseDTO updatedDto = service.update(id, dto, authenticatedUser);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<Void> deleteCampaign(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {
        service.delete(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
