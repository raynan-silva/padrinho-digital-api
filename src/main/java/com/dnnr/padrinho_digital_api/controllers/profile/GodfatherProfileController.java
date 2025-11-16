package com.dnnr.padrinho_digital_api.controllers.profile;

import com.dnnr.padrinho_digital_api.dtos.godfather.GodfatherProfileStatsDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.godfather.GodfatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
public class GodfatherProfileController {
    private final GodfatherService godfatherService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('PADRINHO')")
    public ResponseEntity<GodfatherProfileStatsDTO> getProfileStats(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        GodfatherProfileStatsDTO stats = godfatherService.getGodfatherStats(authenticatedUser);
        return ResponseEntity.ok(stats);
    }
}
