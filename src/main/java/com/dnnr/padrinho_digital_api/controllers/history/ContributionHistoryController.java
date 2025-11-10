package com.dnnr.padrinho_digital_api.controllers.history;

import com.dnnr.padrinho_digital_api.dtos.history.ContributionHistoryDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.history.ContributionHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history/contributions")
@RequiredArgsConstructor
public class ContributionHistoryController {

    private final ContributionHistoryService historyService;

    @GetMapping
    public ResponseEntity<Page<ContributionHistoryDTO>> getContributionHistory(
            @AuthenticationPrincipal User authenticatedUser,
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {

        // O PageableDefault já garante a ordenação por data
        Page<ContributionHistoryDTO> historyPage = historyService.getHistory(authenticatedUser, pageable);
        return ResponseEntity.ok(historyPage);
    }
}