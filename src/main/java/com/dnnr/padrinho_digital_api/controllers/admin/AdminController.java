package com.dnnr.padrinho_digital_api.controllers.admin;

import com.dnnr.padrinho_digital_api.dtos.ong.OngsPendingResponseDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/ongs/pending")
    public ResponseEntity<Page<OngsPendingResponseDTO>> getOngsPeding(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<OngsPendingResponseDTO> ongPage = adminService.getOngsPending(pageable);
        return ResponseEntity.ok(ongPage);
    }

    @PatchMapping("/ong/approve/{id}")
    public ResponseEntity<Void> approveOng(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        adminService.approveOng(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/ong/reject/{id}")
    public ResponseEntity<Void> rejectOng(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        adminService.rejectOng(id, user);
        return ResponseEntity.noContent().build();
    }
}
