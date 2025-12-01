package com.dnnr.padrinho_digital_api.controllers.sponsorship;

import com.dnnr.padrinho_digital_api.dtos.sponsorship.CreateSponsorshipDTO;
import com.dnnr.padrinho_digital_api.dtos.sponsorship.SponsorshipDashboardDTO;
import com.dnnr.padrinho_digital_api.dtos.sponsorship.SponsorshipResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.sponsorship.UpdateSponsorshipDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.sponsorship.SponsorshipService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("sponsorships")
public class SponsorshipController {

    @Autowired
    private SponsorshipService service;

    @PostMapping
    public ResponseEntity<SponsorshipResponseDTO> createSponsorship(
            @RequestBody @Valid CreateSponsorshipDTO data,
            @AuthenticationPrincipal User authenticatedUser) {

        SponsorshipResponseDTO response = service.createSponsorship(data, authenticatedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<SponsorshipResponseDTO> updateSponsorshipStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSponsorshipDTO data,
            @AuthenticationPrincipal User authenticatedUser) {

        SponsorshipResponseDTO response = service.updateSponsorshipStatus(id, data, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<SponsorshipResponseDTO>> listSponsorships(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable,
            @RequestParam(required = false) Long petId, // Usado por Admin
            @RequestParam(required = false) Long ongId, // Usado por Admin
            @AuthenticationPrincipal User authenticatedUser) {

        Page<SponsorshipResponseDTO> response = service.listSponsorships(pageable, petId, ongId, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SponsorshipResponseDTO> getSponsorshipById(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        SponsorshipResponseDTO response = service.getSponsorshipById(id, authenticatedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> disableSponsorship(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        service.disableSponsorship(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dashboard")
    @PreAuthorize("hasRole('PADRINHO')") // Garante que apenas padrinhos acessem
    public ResponseEntity<SponsorshipDashboardDTO> getDashboard(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        SponsorshipDashboardDTO dashboard = service.getSponsorshipDashboard(id, authenticatedUser);
        return ResponseEntity.ok(dashboard);
    }
}