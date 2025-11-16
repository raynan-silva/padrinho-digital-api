package com.dnnr.padrinho_digital_api.controllers.gamification;

import com.dnnr.padrinho_digital_api.dtos.gamification.GamificationStatusDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.gamification.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gamification")
@RequiredArgsConstructor
public class GamificationController {
    private final GamificationService gamificationService;

    /**
     * Retorna o status de gamificação do padrinho logado.
     * Inclui nível atual, próximos níveis, selos conquistados e selos a conquistar.
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('PADRINHO')")
    public ResponseEntity<GamificationStatusDTO> getGamificationStatus(
            @AuthenticationPrincipal User authenticatedUser) {

        GamificationStatusDTO status = gamificationService.getGamificationStatus(authenticatedUser);
        return ResponseEntity.ok(status);
    }
}
