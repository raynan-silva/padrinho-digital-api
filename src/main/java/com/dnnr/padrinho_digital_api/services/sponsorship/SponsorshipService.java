package com.dnnr.padrinho_digital_api.services.sponsorship;

import com.dnnr.padrinho_digital_api.dtos.sponsorship.*;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Cost;
import com.dnnr.padrinho_digital_api.entities.pet.CostHistory;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.BusinessException;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
        Sponsorship sponsorship = repository.findByGodfatherAndPet(godfather, pet)
                .orElseGet(() -> {
                    // Se o vínculo não existe (primeiro apadrinhamento de todos), crie-o.
                    Sponsorship newSponsorship = new Sponsorship();
                    newSponsorship.setGodfather(godfather);
                    newSponsorship.setPet(pet);
                    return repository.save(newSponsorship);
                }
        );

        Optional<SponsorshipHistory> activeHistory = historyRepository.findCurrentHistoryBySponsorshipId(sponsorship.getId());

        if (activeHistory.isPresent()) {
            // Se encontrou um histórico sem data fim, o apadrinhamento já está ativo.
            throw new SponsorshipException("Você já possui um apadrinhamento ativo para este pet.");
        }

        SponsorshipHistory newHistory = new SponsorshipHistory();
        newHistory.setSponsorship(sponsorship); // Vincula ao "pai" encontrado ou criado
        newHistory.setStatus(SponsorshipStatus.ATIVO);
        newHistory.setMonthlyAmount(data.monthlyAmount());
        newHistory.setStartDate(data.startDate());
        newHistory.setEndDate(null);

        SponsorshipHistory savedHistory = historyRepository.save(newHistory);

        gamificationService.checkAndApplyMilestones(godfather.getId());

        // Retorna a view completa
        return new SponsorshipResponseDTO(sponsorship, savedHistory);
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

        // Agora buscamos Page<SponsorshipHistory>
        Page<SponsorshipHistory> historyPage;

        switch (authenticatedUser.getRole()) {
            case PADRINHO:
                Godfather godfather = getGodfatherFromUser(authenticatedUser);
                // Chama o novo método do historyRepository
                historyPage = historyRepository.findBySponsorshipGodfatherAndEndDateIsNull(godfather, pageable);
                break;

            case GERENTE, VOLUNTARIO:
                Ong ong = petService.getOngFromUser(authenticatedUser);
                // Chama o novo método do historyRepository
                historyPage = historyRepository.findBySponsorshipPetOngAndEndDateIsNull(ong, pageable);
                break;

            case ADMIN:
                if (petId != null) {
                    Pet pet = petRepository.findById(petId)
                            .orElseThrow(() -> new NotFoundException("Pet com ID " + petId + " não encontrado."));
                    // Chama o novo método do historyRepository
                    historyPage = historyRepository.findBySponsorshipPetAndEndDateIsNull(pet, pageable);

                } else if (ongId != null) {
                    Ong admminOng = ongRepository.findById(ongId)
                            .orElseThrow(() -> new EntityNotFoundException("ONG com ID " + ongId + " não encontrada."));
                    // Chama o novo método do historyRepository
                    historyPage = historyRepository.findBySponsorshipPetOngAndEndDateIsNull(admminOng, pageable);

                } else {
                    throw new SponsorshipException("Admin deve prover 'petId' ou 'ongId' para filtrar.");
                }
                break;

            default:
                throw new AccessDeniedException("Role não autorizada.");
        }

        // Converte Page<SponsorshipHistory> para Page<SponsorshipResponseDTO>
        // O N+1 foi resolvido!
        return historyPage.map(history -> new SponsorshipResponseDTO(history.getSponsorship(), history));
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

    @Transactional
    public void disableSponsorship(Long sponsorshipId, User authenticatedUser) {
        Sponsorship sponsorship = repository.findById(sponsorshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Apadrinhamento não encontrado"));

        checkPermission(sponsorship, authenticatedUser);

        SponsorshipHistory activeHistory = sponsorship.getHistory().stream()
                .filter(h -> h.getStatus() == SponsorshipStatus.ATIVO)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Não foi possível encontrar um apadrinhamento ativo para encerrar."));

        activeHistory.setStatus(SponsorshipStatus.ENCERRADO);
        activeHistory.setEndDate(LocalDate.now());

        historyRepository.save(activeHistory);
    }

    @Transactional(readOnly = true)
    public SponsorshipDashboardDTO getSponsorshipDashboard(Long sponsorshipId, User authenticatedUser) {
        // 1. Busca o Apadrinhamento e valida existência
        Sponsorship sponsorship = repository.findById(sponsorshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Apadrinhamento com Id: " + sponsorshipId + "não encontrado."));

        // 2. Validação de Segurança: O usuário logado é realmente o padrinho?
        // (Assumindo que Godfather tem relação com User)
        if (!sponsorship.getGodfather().getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para visualizar este apadrinhamento.");
        }

        Pet pet = sponsorship.getPet();

        // --- 1. ANIMAL DATA ---
        String profileImage = (pet.getPhotos() != null && !pet.getPhotos().isEmpty())
                ? pet.getPhotos().get(0).getPhoto()
                : null;

        AnimalDataDTO animalData = new AnimalDataDTO(
                pet.getName(),
                pet.getBreed(), // Ex: "Pet - Labrador"
                calculateAge(pet.getBirthDate()),
                sponsorship.getCreatedAt().toLocalDate(),
                profileImage
        );

        // --- 2. FINANCIAL DATA ---

        // A. Contribuição Atual (Pega o histórico onde endDate é nulo)

        BigDecimal currentContribution = sponsorship.getHistory().stream()
                .filter(h -> h.getEndDate() == null)
                .findFirst()
                .map(SponsorshipHistory::getMonthlyAmount)
                .orElse(BigDecimal.ZERO);

        // B. Despesas Totais e Detalhes
        BigDecimal totalExpenses = BigDecimal.ZERO;
        List<ExpenseItemDTO> expenseDetails = new ArrayList<>();

        if (pet.getCosts() != null) {
            for (Cost cost : pet.getCosts()) {
                // Pega apenas o custo ATIVO do Pet
                CostHistory activeCost = cost.getHistory().stream()
                        .filter(h -> h.getEndDate() == null)
                        .findFirst()
                        .orElse(null);

                if (activeCost != null) {
                    totalExpenses = totalExpenses.add(activeCost.getMonthlyAmount());

                    expenseDetails.add(new ExpenseItemDTO(
                            activeCost.getName(), // Ex: "Food"
                            activeCost.getStartDate(),
                            activeCost.getDescription(),
                            activeCost.getMonthlyAmount()
                    ));
                }
            }
        }

        // C. Cálculo de Cobertura (%)
        String coveragePercentage = "0%";
        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = currentContribution
                    .divide(totalExpenses, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            // Se passar de 100%, fixa em 100% (opcional)
            if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                coveragePercentage = "100%";
            } else {
                coveragePercentage = percentage.intValue() + "%";
            }
        } else if (currentContribution.compareTo(BigDecimal.ZERO) > 0) {
            // Se não tem despesas cadastradas, mas tem doação, a cobertura é total
            coveragePercentage = "100%";
        }

        FinancialDataDTO financialData = new FinancialDataDTO(
                totalExpenses,
                currentContribution,
                coveragePercentage,
                expenseDetails
        );

        // --- 3. GALLERY DATA ---
        List<GalleryItemDTO> gallery = new ArrayList<>();
        if (pet.getPhotos() != null) {
            gallery = pet.getPhotos().stream()
                    // Ordena por ID decrescente (fotos novas primeiro)
                    .sorted(Comparator.comparing(Photo::getId).reversed())
                    .map(photo -> new GalleryItemDTO(
                            photo.getId(),
                            LocalDate.now(), // Placeholder: Adicione 'createdAt' na entidade Photo para ter a data real
                            photo.getPhoto() // Retorna a string Base64
                    ))
                    .toList();
        }

        return new SponsorshipDashboardDTO(animalData, financialData, gallery);
    }

    // Método auxiliar para calcular idade
    private String calculateAge(LocalDate birthDate) {
        if (birthDate == null) return "Sem idade";
        int years = Period.between(birthDate, LocalDate.now()).getYears();
        if (years == 0) {
            int months = Period.between(birthDate, LocalDate.now()).getMonths();
            return months + " meses";
        }
        return years + " anos";
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Método auxiliar para garantir que o usuário logado
     * é o padrinho associado a este apadrinhamento.
     */
    private void checkPermission(Sponsorship sponsorship, User authenticatedUser) {
        // (Assumindo que Godfather tem um campo 'user')
        if (!sponsorship.getGodfather().getUser().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("Você não tem permissão para modificar este apadrinhamento.");
        }
    }

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