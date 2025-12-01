package com.dnnr.padrinho_digital_api.controllers.godfather;

import com.dnnr.padrinho_digital_api.dtos.godfather.GodfatherDashboardDTO;
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
@RequestMapping("/godfather")
@RequiredArgsConstructor
public class GodfatherController {

    private final GodfatherService service;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('PADRINHO')")
    public ResponseEntity<GodfatherDashboardDTO> getDashboard(
            @AuthenticationPrincipal User authenticatedUser) {

        GodfatherDashboardDTO dashboardData = service.getDashboardData(authenticatedUser);
        return ResponseEntity.ok(dashboardData);
    }
}
