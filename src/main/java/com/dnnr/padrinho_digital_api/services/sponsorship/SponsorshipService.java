package com.dnnr.padrinho_digital_api.services.sponsorship;

import com.dnnr.padrinho_digital_api.dtos.sponsorship.CreateSponsorshipDTO;
import com.dnnr.padrinho_digital_api.dtos.sponsorship.SponsorshipResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.sponsorship.UpdateSponsorshipDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.SponsorshipException;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.repositories.pet.PetRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.services.gamification.GamificationService;
import com.dnnr.padrinho_digital_api.services.pet.PetService; // Para o metodo getOngFromUser
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SponsorshipService {

    private final SponsorshipRepository repository;
    private final SponsorshipHistoryRepository historyRepository;
    private final GodfatherRepository godfatherRepository;
    private final PetRepository petRepository;
    private final OngRepository ongRepository;
    private final PetService petService;
    private final GamificationService gamificationService;

    /**
     * CREATE (Padrinho)
     */
    @Transactional
    public SponsorshipResponseDTO createSponsorship(CreateSponsorshipDTO data, User authenticatedUser) {
        Godfather godfather = getGodfatherFromUser(authenticatedUser);

        Pet pet = petRepository.findById(data.petId())
                .orElseThrow(() -> new NotFoundException("Pet com ID " + data.petId() + " não encontrado."));

        // Regra: Padrinho não pode apadrinhar o mesmo pet duas vezes
        repository.findByGodfatherAndPet(godfather, pet).ifPresent(s -> {
            throw new SponsorshipException("Você já apadrinha este pet.");
        });

        // 1. Cria a relação principal
        Sponsorship newSponsorship = new Sponsorship();
        newSponsorship.setGodfather(godfather);
        newSponsorship.setPet(pet);
        Sponsorship savedSponsorship = repository.save(newSponsorship);

        // 2. Cria o primeiro período (ATIVO)
        SponsorshipHistory firstHistory = new SponsorshipHistory();
        firstHistory.setSponsorship(savedSponsorship);
        firstHistory.setStatus(SponsorshipStatus.ATIVO);
        firstHistory.setMonthlyAmount(data.monthlyAmount());
        firstHistory.setStartDate(data.startDate());
        firstHistory.setEndDate(null);

        SponsorshipHistory savedHistory = historyRepository.save(firstHistory);

        gamificationService.checkAndApplyMilestones(godfather.getId());

        // Retorna a view completa
        return new SponsorshipResponseDTO(savedSponsorship, savedHistory);
    }

    /**
     * UPDATE (Padrinho)
     * Pausa, Cancela ou Retoma um apadrinhamento
     */
    @Transactional
    public SponsorshipResponseDTO updateSponsorshipStatus(Long sponsorshipId, UpdateSponsorshipDTO data, User authenticatedUser) {
        Godfather godfather = getGodfatherFromUser(authenticatedUser);

        Sponsorship sponsorship = repository.findById(sponsorshipId)
                .orElseThrow(() -> new EntityNotFoundException("Apadrinhamento com ID " + sponsorshipId + " não encontrado."));

        // Segurança: O padrinho autenticado é dono deste apadrinhamento?
        if (!sponsorship.getGodfather().equals(godfather)) {
            throw new AccessDeniedException("Você não tem permissão para alterar este apadrinhamento.");
        }

        // Busca o período ATUAL (endDate IS NULL)
        SponsorshipHistory currentHistory = historyRepository.findCurrentHistoryBySponsorshipId(sponsorshipId)
                .orElseThrow(() -> new IllegalStateException("Apadrinhamento não possui um período ativo."));

        // Regra: Não pode mudar para o status que já está
        if (currentHistory.getStatus() == data.newStatus()) {
            throw new SponsorshipException("O apadrinhamento já está com o status " + data.newStatus());
        }

        // 1. "Fecha" o período atual
        currentHistory.setEndDate(LocalDate.now().minusDays(1)); // Encerra no dia anterior
        historyRepository.save(currentHistory);

        // 2. Cria o novo período
        SponsorshipHistory newHistory = new SponsorshipHistory();
        newHistory.setSponsorship(sponsorship);
        newHistory.setStatus(data.newStatus());
        newHistory.setStartDate(LocalDate.now());
        newHistory.setEndDate(null); // Novo período atual
        newHistory.setNotes(data.notes());

        // Define o valor
        if (data.newStatus() == SponsorshipStatus.ATIVO) {
            // Se está reativando, usa o novo valor ou, se nulo, o valor anterior
            newHistory.setMonthlyAmount(
                    data.newAmount() != null ? data.newAmount() : currentHistory.getMonthlyAmount()
            );
        } else {
            // Se PAUSADO ou CANCELADO, o valor é 0
            newHistory.setMonthlyAmount(BigDecimal.ZERO);
        }

        SponsorshipHistory savedNewHistory = historyRepository.save(newHistory);

        // Recarrega o sponsorship com todo o histórico
        Sponsorship updatedSponsorship = repository.findByIdWithHistory(sponsorshipId).get();
        return new SponsorshipResponseDTO(updatedSponsorship, savedNewHistory);
    }

    /**
     * GET (Listagem Paginada)
     * Filtra o resultado baseado no Role do usuário.
     */
    @Transactional(readOnly = true)
    public Page<SponsorshipResponseDTO> listSponsorships(Pageable pageable, Long petId, Long ongId, User authenticatedUser) {
        Page<Sponsorship> sponsorshipPage;

        switch (authenticatedUser.getRole()) {
            case PADRINHO:
                Godfather godfather = getGodfatherFromUser(authenticatedUser);
                sponsorshipPage = repository.findByGodfather(godfather, pageable);
                break;

            case GERENTE, VOLUNTARIO:
                Ong ong = petService.getOngFromUser(authenticatedUser);
                sponsorshipPage = repository.findByPet_Ong(ong, pageable);
                break;

            case ADMIN:
                if (petId != null) {
                    Pet pet = petRepository.findById(petId)
                            .orElseThrow(() -> new NotFoundException("Pet com ID " + petId + " não encontrado."));
                    sponsorshipPage = repository.findByPet(pet, pageable);
                } else if (ongId != null) {
                    Ong admminOng = ongRepository.findById(ongId)
                            .orElseThrow(() -> new EntityNotFoundException("ONG com ID " + ongId + " não encontrada."));
                    sponsorshipPage = repository.findByPet_Ong(admminOng, pageable);
                } else {
                    throw new SponsorshipException("Admin deve prover 'petId' ou 'ongId' para filtrar.");
                }
                break;

            default:
                throw new AccessDeniedException("Role não autorizada.");
        }

        // Converte Page<Sponsorship> para Page<SponsorshipResponseDTO>
        return sponsorshipPage.map(s -> {
            // Esta é uma consulta N+1. Para performance, seria necessário otimizar.
            // Mas para o CRUD funcionar, é o suficiente.
            SponsorshipHistory current = historyRepository.findCurrentHistoryBySponsorshipId(s.getId()).orElse(null);
            if (current == null) return null; // Ignora apadrinhamentos sem histórico (não deve acontecer)
            return new SponsorshipResponseDTO(s, current);
        });
    }

    /**
     * GET (By ID)
     * Verifica a permissão de visualização.
     */
    @Transactional(readOnly = true)
    public SponsorshipResponseDTO getSponsorshipById(Long sponsorshipId, User authenticatedUser) {
        Sponsorship sponsorship = repository.findByIdWithHistory(sponsorshipId)
                .orElseThrow(() -> new EntityNotFoundException("Apadrinhamento com ID " + sponsorshipId + " não encontrado."));

        // Valida a permissão de visualização
        checkViewPermission(authenticatedUser, sponsorship);

        SponsorshipHistory current = historyRepository.findCurrentHistoryBySponsorshipId(sponsorshipId)
                .orElseThrow(() -> new IllegalStateException("Apadrinhamento não possui um período ativo."));

        return new SponsorshipResponseDTO(sponsorship, current);
    }

    // --- MÉTODOS AUXILIARES ---

    private Godfather getGodfatherFromUser(User user) {
        return godfatherRepository.findByUser(user)
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não é um Padrinho."));
    }

    private void checkViewPermission(User user, Sponsorship sponsorship) {
        switch (user.getRole()) {
            case PADRINHO:
                Godfather godfather = getGodfatherFromUser(user);
                if (!sponsorship.getGodfather().equals(godfather)) {
                    throw new AccessDeniedException("Você não pode ver apadrinhamentos de outro padrinho.");
                }
                break;

            case GERENTE, VOLUNTARIO:
                Ong ManagerOng = petService.getOngFromUser(user);
                if (!sponsorship.getPet().getOng().equals(ManagerOng)) {
                    throw new AccessDeniedException("Você só pode ver apadrinhamentos de pets da sua ONG.");
                }
                break;

            case ADMIN:
                // Admin pode ver tudo
                break;

            default:
                throw new AccessDeniedException("Role não autorizada.");
        }
    }


}