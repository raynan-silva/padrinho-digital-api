package com.dnnr.padrinho_digital_api.controllers.ong;

import com.dnnr.padrinho_digital_api.dtos.ong.OngDashboardDTO;
import com.dnnr.padrinho_digital_api.dtos.ong.OngProfileDTO;
import com.dnnr.padrinho_digital_api.dtos.ong.OngResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.ong.UpdateOngDTO;
import com.dnnr.padrinho_digital_api.dtos.photo.AddPhotosDTO;
import com.dnnr.padrinho_digital_api.dtos.report.OngReportDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.ong.OngService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ong")
public class OngController {
    @Autowired
    OngService ongService;

    /**
     * READ (Paginated)
     * Ex: GET /pet?page=0&size=10&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<Page<OngResponseDTO>> getAllOngs(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<OngResponseDTO> ongPage = ongService.getAllOngs(pageable);
        return ResponseEntity.ok(ongPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OngResponseDTO> getOngById(@PathVariable Long id) {
        OngResponseDTO ong = ongService.getOngById(id);
        return ResponseEntity.ok(ong);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<OngProfileDTO> getProfile(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        return ResponseEntity.ok(ongService.getProfile(authenticatedUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OngResponseDTO> updateOng(@PathVariable Long id,
                                                    @RequestBody @Valid UpdateOngDTO data,
                                                    @AuthenticationPrincipal User authenticatedUser) {
        OngResponseDTO updatedOng = ongService.updateOng(id, data, authenticatedUser);
        return ResponseEntity.ok(updatedOng);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOng(@PathVariable Long id,
                                          @AuthenticationPrincipal User authenticatedUser) {
        ongService.deleteOng(id, authenticatedUser);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<OngResponseDTO> addPhotosToOng(
            @PathVariable Long id,
            @RequestBody @Valid AddPhotosDTO data,
            @AuthenticationPrincipal User authenticatedUser) {

        OngResponseDTO updatedOng = ongService.addPhotos(id, data.photos(), authenticatedUser);
        return ResponseEntity.ok(updatedOng);
    }

    @DeleteMapping("/{id}/photos/{photoId}")
    public ResponseEntity<Void> removePhotoFromOng(
            @PathVariable Long id,
            @PathVariable Long photoId,
            @AuthenticationPrincipal User authenticatedUser) {

        ongService.removePhoto(id, photoId, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<OngDashboardDTO> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ongService.getDashboardData(user));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<OngReportDTO> getReport(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ongService.getReportData(user));
    }
}
