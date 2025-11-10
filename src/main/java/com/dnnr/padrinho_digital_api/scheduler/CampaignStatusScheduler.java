package com.dnnr.padrinho_digital_api.scheduler;

import com.dnnr.padrinho_digital_api.services.donation_campaign.DonationCampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CampaignStatusScheduler {

    private final DonationCampaignService campaignService;

    /**
     * Regra 6: Roda todo dia à 00:01 para fechar campanhas que
     * expiraram no dia anterior (endDate < LocalDate.now()).
     */
    @Scheduled(cron = "0 1 0 * * ?") // 00:01:00 todo dia
    public void closeExpiredCampaignsJob() {
        log.info("Iniciando job para fechar campanhas expiradas...");
        try {
            campaignService.closeExpiredCampaigns();
            log.info("Job finalizado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao executar job de fechamento de campanhas: {}", e.getMessage(), e);
        }
    }
}
