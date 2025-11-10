package com.dnnr.padrinho_digital_api.services.donation_campaign;

import com.dnnr.padrinho_digital_api.dtos.donation_campaign.CreateDonationCampaignDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.DonationCampaignResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.donation_campaign.UpdateDonationCampaignDTO;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.CampaignStatus;
import com.dnnr.padrinho_digital_api.entities.donation_campaign.DonationCampaign;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.Volunteer;
import com.dnnr.padrinho_digital_api.exceptions.BusinessException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.donation_campaign.DonationCampaignRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class DonationCampaignService {

    @Autowired
    DonationCampaignRepository repository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    VolunteerRepository volunteerRepository;

    // GET ALL (by Manager's ONG)
    @Transactional(readOnly = true)
    public Page<DonationCampaignResponseDTO> findAllByOng(User authenticatedUser, Pageable pageable) {
        Ong ong = getOngFromUser(authenticatedUser); // Regra 3
        Page<DonationCampaign> page = repository.findAllByOngId(ong.getId(), pageable);
        return page.map(DonationCampaignResponseDTO::new);
    }

    // GET ALL ATIVAS (Público, para Padrinhos)
    @Transactional(readOnly = true)
    public Page<DonationCampaignResponseDTO> findAllActive(Pageable pageable) {
        Page<DonationCampaign> page = repository.findAllByStatus(CampaignStatus.ATIVA, pageable);
        return page.map(DonationCampaignResponseDTO::new);
    }

    // GET BY ID (Manager only)
    @Transactional(readOnly = true)
    public DonationCampaignResponseDTO findById(Long id, User authenticatedUser) {
        DonationCampaign campaign = findCampaignByIdAndCheckPermission(id, authenticatedUser);
        return new DonationCampaignResponseDTO(campaign);
    }

    @Transactional(readOnly = true)
    public DonationCampaignResponseDTO findByIdGodfather(Long id) {
        DonationCampaign campaign = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Campanha não encontrada")
        );
        return new DonationCampaignResponseDTO(campaign);
    }

    // CREATE
    @Transactional
    public DonationCampaignResponseDTO create(CreateDonationCampaignDTO dto, User authenticatedUser) {
        Ong ong = getOngFromUser(authenticatedUser); // Regra 3

        DonationCampaign campaign = new DonationCampaign();
        campaign.setTitle(dto.title());
        campaign.setDescription(dto.description());
        campaign.setStartDate(dto.startDate());
        campaign.setEndDate(dto.endDate());
        campaign.setValueTarget(dto.valueTarget());
        campaign.setPhoto(dto.photo());
        campaign.setOng(ong);
        campaign.setStatus(CampaignStatus.ATIVA); // Padrão

        DonationCampaign savedCampaign = repository.save(campaign);
        return new DonationCampaignResponseDTO(savedCampaign);
    }

    // UPDATE (Manager only)
    @Transactional
    public DonationCampaignResponseDTO update(Long id, UpdateDonationCampaignDTO dto, User authenticatedUser) {
        DonationCampaign campaign = findCampaignByIdAndCheckPermission(id, authenticatedUser);

        if (campaign.getStatus() == CampaignStatus.CONCLUIDA) {
            throw new BusinessException("Não é possível alterar uma campanha concluída.");
        }

        if (dto.title() != null) campaign.setTitle(dto.title());
        if (dto.description() != null) campaign.setDescription(dto.description());
        if (dto.endDate() != null) campaign.setEndDate(dto.endDate());
        if (dto.status() != null) campaign.setStatus(dto.status());
        if (dto.photo() != null) campaign.setPhoto(dto.photo());

        DonationCampaign updatedCampaign = repository.save(campaign);
        return new DonationCampaignResponseDTO(updatedCampaign);
    }

    // DELETE (Lógico)
    @Transactional
    public void delete(Long id, User authenticatedUser) {
        DonationCampaign campaign = findCampaignByIdAndCheckPermission(id, authenticatedUser);

        if (campaign.getStatus() == CampaignStatus.CONCLUIDA) {
            throw new BusinessException("Não é possível cancelar uma campanha concluída.");
        }

        if (campaign.getAmountCollected().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Não é possível cancelar uma campanha que já recebeu doações. Considere 'Pausar'.");
        }

        campaign.setStatus(CampaignStatus.CANCELADA);
        repository.save(campaign);
    }

    // --- Método para o Scheduler ---
    @Transactional
    public void closeExpiredCampaigns() {
        repository.closeExpiredCampaigns(LocalDate.now());
    }

    // --- Helper Interno ---
    private DonationCampaign findCampaignByIdAndCheckPermission(Long id, User user) {
        Ong ong = getOngFromUser(user); // Regra 3
        return repository.findByIdAndOngId(id, ong.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Campanha não encontrada ou não pertence à sua ONG (ID: " + id + ")"));
    }

    public Ong getOngFromUser(User user) {
        // Tenta encontrar como Gerente
        Optional<Manager> manager = managerRepository.findByUser(user);
        if (manager.isPresent()) {
            return manager.get().getOng();
        }

        // Se não for Gerente, tenta encontrar como Voluntário
        Optional<Volunteer> volunteer = volunteerRepository.findByUser(user);
        if (volunteer.isPresent()) {
            return volunteer.get().getOng();
        }

        // Se o usuário (ex: um Admin) não estiver ligado a uma Manager ou Volunteer
        throw new AccessDeniedException("Usuário autenticado não está associado a nenhuma ONG.");
    }

}
