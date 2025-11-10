package com.dnnr.padrinho_digital_api.services.donation_campaign;

import com.dnnr.padrinho_digital_api.dtos.donation_campaign.CreateDonationDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.DonationResponseDTO;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.CampaignStatus;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.Donation;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.DonationCampaign;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.BusinessException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.donation_campaign.DonationCampaignRepository;
import com.dnnr.padrinho_digital_api.repositories.donation_campaign.DonationRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class DonationService {
    @Autowired
    DonationRepository donationRepository;
    @Autowired
    DonationCampaignRepository campaignRepository;
    @Autowired
    GodfatherRepository godfatherRepository;

    @Transactional
    public DonationResponseDTO createDonation(CreateDonationDTO dto, User authenticatedUser) {
        // 1. Encontrar o Padrinho (precisa de um helper ou repository)
        Godfather godfather = godfatherRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil de Padrinho não encontrado."));

        // 2. Encontrar a Campanha
        DonationCampaign campaign = campaignRepository.findById(dto.campaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada (ID: " + dto.campaignId() + ")"));

        // 3. Validar Regras de Negócio
        if (campaign.getStatus() != CampaignStatus.ATIVA) {
            throw new BusinessException("Esta campanha não está ativa para doações.");
        }
        if (campaign.getEndDate() != null && LocalDate.now().isAfter(campaign.getEndDate())) {
            throw new BusinessException("Esta campanha já foi encerrada.");
        }

        // 4. Atualizar o valor coletado na campanha
        BigDecimal newAmountCollected = campaign.getAmountCollected().add(dto.amount());
        campaign.setAmountCollected(newAmountCollected);

        // 5. Regra 4: Verificar se bateu a meta
        if (newAmountCollected.compareTo(campaign.getValueTarget()) >= 0) {
            campaign.setStatus(CampaignStatus.CONCLUIDA);
        }

        // Salva a campanha atualizada
        campaignRepository.save(campaign);

        // 6. Criar e salvar a Doação
        Donation donation = new Donation();
        donation.setAmount(dto.amount());
        donation.setDate(LocalDate.now());
        donation.setGodfather(godfather);
        donation.setDonationCampaign(campaign);

        Donation savedDonation = donationRepository.save(donation);

        return new DonationResponseDTO(savedDonation);
    }
}
