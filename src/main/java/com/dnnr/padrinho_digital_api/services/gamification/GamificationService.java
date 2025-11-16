package com.dnnr.padrinho_digital_api.services.gamification;

import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherSeal;
import com.dnnr.padrinho_digital_api.entities.godfather.Seal;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.UserStatus;
import com.dnnr.padrinho_digital_api.repositories.donation_campaign.DonationRepository;
import com.dnnr.padrinho_digital_api.repositories.godfather.GodfatherLevelRepository;
import com.dnnr.padrinho_digital_api.repositories.godfather.GodfatherSealRepository;
import com.dnnr.padrinho_digital_api.repositories.godfather.SealRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationService {

    private final GodfatherRepository godfatherRepository;
    private final GodfatherLevelRepository levelRepository;
    private final SealRepository sealRepository;
    private final GodfatherSealRepository godfatherSealRepository;
    private final DonationRepository donationRepository;
    private final SponsorshipHistoryRepository sponsorshipHistoryRepository;

    /**
     * Define o Nível 1 e o Selo de Cadastro para um novo Padrinho.
     * Chame este método quando o Padrinho for criado.
     */
    @Transactional
    public void initializeNewGodfather(Godfather godfather) {
        // 1. Define Nível 1
        GodfatherLevel level1 = levelRepository.findByOrder(1)
                .orElseThrow(() -> new EntityNotFoundException("Nível de Padrinho '1' não encontrado no banco."));
        godfather.setCurrentLevel(level1);
        godfatherRepository.save(godfather);

        // 2. Concede Selo de Cadastro
        Seal defaultSeal = sealRepository.findByTriggerMetric("DEFAULT")
                .orElseThrow(() -> new EntityNotFoundException("Selo 'DEFAULT' não encontrado no banco."));

        if (!godfatherSealRepository.existsByGodfatherIdAndSealId(godfather.getId(), defaultSeal.getId())) {
            godfatherSealRepository.save(new GodfatherSeal(godfather, defaultSeal));
        }
    }

    /**
     * Verifica e atualiza níveis e selos.
     * Chame este método APÓS uma doação ou apadrinhamento.
     */
    @Transactional
    public void checkAndApplyMilestones(Long godfatherId) {
        Godfather godfather = godfatherRepository.findById(godfatherId)
                .orElseThrow(() -> new EntityNotFoundException("Padrinho não encontrado: " + godfatherId));

        // 1. Recalcular métricas
        BigDecimal totalDonated = donationRepository.sumAmountByGodfatherId(godfatherId).orElse(BigDecimal.ZERO);
        long donationCount = donationRepository.countByGodfatherId(godfatherId);
        long activeSponsorships = sponsorshipHistoryRepository.countBySponsorshipGodfatherIdAndStatus(godfatherId, SponsorshipStatus.ATIVO);

        // 2. Checar por Level Up
        checkLevelUp(godfather, totalDonated);

        // 3. Checar por novos Selos
        checkNewSeals(godfather, totalDonated, donationCount, activeSponsorships);
    }

    private void checkLevelUp(Godfather godfather, BigDecimal totalDonated) {
        GodfatherLevel currentLevel = godfather.getCurrentLevel();
        GodfatherLevel nextLevel = levelRepository.findByOrder(currentLevel.getOrder() + 1).orElse(null);

        // Se existe um próximo nível e o padrinho atingiu o valor
        if (nextLevel != null && totalDonated.compareTo(nextLevel.getRequiredAmount()) >= 0) {
            godfather.setCurrentLevel(nextLevel);
            godfatherRepository.save(godfather);
            log.info("Padrinho {} subiu para o Nível: {}", godfather.getId(), nextLevel.getName());
            // (Aqui você pode disparar um evento/notificação)
        }
    }

    private void checkNewSeals(Godfather godfather, BigDecimal totalDonated, long donationCount, long activeSponsorships) {
        List<Seal> allSeals = sealRepository.findAll();
        List<Long> earnedSealIds = godfatherSealRepository.findAllByGodfatherId(godfather.getId())
                .stream().map(gs -> gs.getSeal().getId()).toList();

        List<GodfatherSeal> newSealsToGrant = new ArrayList<>();

        for (Seal seal : allSeals) {
            if (earnedSealIds.contains(seal.getId())) {
                continue; // Já possui este selo
            }

            boolean grant = false;
            switch (seal.getTriggerMetric()) {
                case "DONATION_TOTAL_AMOUNT":
                    grant = totalDonated.compareTo(seal.getTriggerValue()) >= 0;
                    break;
                case "DONATION_COUNT":
                    grant = donationCount >= seal.getTriggerValue().longValue();
                    break;
                case "SPONSORSHIP_ACTIVE_COUNT":
                    grant = activeSponsorships >= seal.getTriggerValue().longValue();
                    break;
                case "DEFAULT":
                    break; // Selo 'Default' só é dado na criação
            }

            if (grant) {
                newSealsToGrant.add(new GodfatherSeal(godfather, seal));
                log.info("Padrinho {} conquistou o Selo: {}", godfather.getId(), seal.getName());
            }
        }

        if (!newSealsToGrant.isEmpty()) {
            godfatherSealRepository.saveAll(newSealsToGrant);
        }
    }
}