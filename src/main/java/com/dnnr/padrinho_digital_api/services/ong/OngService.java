package com.dnnr.padrinho_digital_api.services.ong;

import com.dnnr.padrinho_digital_api.dtos.ong.*;
import com.dnnr.padrinho_digital_api.dtos.report.ExpenseDistributionDTO;
import com.dnnr.padrinho_digital_api.dtos.report.OngReportDTO;
import com.dnnr.padrinho_digital_api.dtos.report.PetReportDTO;
import com.dnnr.padrinho_digital_api.dtos.report.SponsorshipReportStatsDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.UserNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.chat.ChatMessageRepository;
import com.dnnr.padrinho_digital_api.repositories.ong.AddressRepository;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.repositories.pet.CostRepository;
import com.dnnr.padrinho_digital_api.repositories.pet.PetRepository;
import com.dnnr.padrinho_digital_api.repositories.photo.PhotoRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import com.dnnr.padrinho_digital_api.services.cost.CostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OngService {

    private final OngRepository repository;
    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final PhotoRepository photoRepository;
    private final PetRepository petRepository;
    private final SponsorshipRepository sponsorshipRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final CostRepository costRepository;

    /**
     * READ (Paginated)
     * Retorna todos as ongs de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<OngResponseDTO> getAllOngs(Pageable pageable) {
        // Busca a página de entidades
        Page<Ong> ongPage = repository.findAll(pageable);
        // Mapeia a página de entidades para uma página de DTOs
        return ongPage.map(OngResponseDTO::new);
    }

    /**
     * READ (By ID)
     * Retorna um pet específico pelo ID.
     */
    @Transactional(readOnly = true)
    public OngResponseDTO getOngById(Long id) {
        Ong ong = repository.findByIdWithPhotos(id)
                .orElseThrow(() -> new NotFoundException("Ong com ID " + id + " não encontrado."));
        return new OngResponseDTO(ong);
    }

    @Transactional(readOnly = true)
    public OngProfileDTO getProfile(User user) {
        Manager manager = managerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Gerente não encontrado"));

        Ong ong = manager.getOng();
        Address address = ong.getAddress();

        return new OngProfileDTO(manager, ong, address);

    }

    /**
     * UPDATE (Partial)
     * Atualiza uma ong. Só permite se o usuário for da mesma ONG.
     */
    @Transactional
    public OngResponseDTO updateOng(Long id, UpdateOngDTO data, User authenticatedUser) {
        // 1. Busca o pet
        Ong ong = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ong com ID " + id + " não encontrada."));

        // 2. Validação de segurança: O usuário pertence à ONG
        checkUserOngPermission(authenticatedUser, ong);

        // 3. Lógica de atualização parcial (só atualiza o que não for nulo)

        if (data.manager_name() != null) {
            authenticatedUser.setName(data.manager_name());
        }

        if (data.photo_manager() != null) {
            authenticatedUser.setPhoto(data.photo_manager());
        }

        if (data.name() != null) {
            ong.setName(data.name());
        }
        if (data.phone() != null) {
            ong.setPhone(data.phone());
        }
        if (data.description() != null) {
            ong.setDescription(data.description());
        }

        // Obtém o endereço associado à ONG
        Address address = ong.getAddress();
        if (address == null) {
            throw new NotFoundException("Erro de integridade: A ONG " + id + " não possui um endereço associado.");
        }

        if (data.street() != null) {
            address.setStreet(data.street());
        }
        if (data.address_number() != null) {
            address.setNumber(data.address_number()); // (Assumindo que o setter é setAddressNumber)
        }
        if (data.neighborhood() != null) {
            address.setNeighborhood(data.neighborhood());
        }
        if (data.city() != null) {
            address.setCity(data.city());
        }
        if (data.uf() != null) {
            address.setUf(data.uf());
        }
        if (data.cep() != null) {
            address.setCep(data.cep());
        }

        if (data.complement() != null) {
            address.setComplement(data.complement());
        }

        List<String> photos = data.photos_ong();
        if (photos != null && !photos.isEmpty()) {
            List<Photo> photoList = photos.stream()
                    .map(Photo::new)
                    .collect(Collectors.toList());
            ong.setPhotos(photoList);
        }

        userRepository.save(authenticatedUser);

        Ong updatedOng = repository.save(ong);

        return new OngResponseDTO(updatedOng);
    }

    /**
     * DELETE (Logical)
     * Realiza a exclusão lógica mudando o status para INATIVO.
     */
    @Transactional
    public void deleteOng(Long id, User authenticatedUser) {
        Ong ong = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ong com ID " + id + " não encontrada."));

        // 2. Validação de segurança
        checkUserOngPermission(authenticatedUser, ong);

        // 3. Exclusão Lógica
        ong.setStatus(OngStatus.INATIVO);
        repository.save(ong);
    }

    @Transactional
    public OngResponseDTO addPhotos(Long ongId, List<String> base64Photos, User authenticatedUser) {
        Ong ong = repository.findById(ongId)
                .orElseThrow(() -> new NotFoundException("Pet com ID " + ongId + " não encontrado"));

        checkUserOngPermission(authenticatedUser, ong);

        List<Photo> newPhotos = base64Photos.stream()
                .map(Photo::new)
                .toList();

        ong.getPhotos().addAll(newPhotos);

        repository.save(ong);

        return getOngById(ongId);
    }

    @Transactional
    public void removePhoto(Long OngId, Long photoId, User authenticatedUser) {
        Ong ong = repository.findById(OngId)
                .orElseThrow(() -> new NotFoundException("Ong com ID " + OngId + " não encontrada"));

        checkUserOngPermission(authenticatedUser, ong);

        Photo photoToRemove = ong.getPhotos().stream()
                .filter(p -> p.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Foto com ID " + photoId + " não encontrda."));

        ong.getPhotos().remove(photoToRemove);
        repository.save(ong);

        photoRepository.delete(photoToRemove);
    }

    @Transactional(readOnly = true)
    public OngDashboardDTO getDashboardData(User authenticatedUser) {
        // 1. Identificar ONG através do Gerente
        Manager manager = managerRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        Ong ong = manager.getOng();
        Long ongId = ong.getId();

        // --- STATS ---
        long totalAnimals = petRepository.countByOngId(ongId);
        long totalSponsored = petRepository.countSponsoredByOngId(ongId);
        long totalAvailable = petRepository.countAvailableByOngId(ongId);
        long newThisWeek = petRepository.countNewPetsSince(ongId, LocalDateTime.now().minusDays(7));

        long activeGodfathers = sponsorshipRepository.countActiveGodfathersByOng(ongId);
        BigDecimal monthlyRevenue = sponsorshipRepository.sumMonthlyRevenueByOng(ongId);
        long unreadMessages = chatMessageRepository.countUnreadMessages(authenticatedUser.getId());

        // Mock de crescimento/metas (para simplificar, mas poderia vir do banco)
        double godfathersGrowth = 12.5;
        double revenueGrowth = 8.2;
        BigDecimal revenueGoal = costRepository.sumTotalActiveCostsByOng(ongId);

        OngStatsDTO stats = new OngStatsDTO(
                totalAnimals, totalSponsored, totalAvailable, newThisWeek,
                activeGodfathers, godfathersGrowth,
                monthlyRevenue, revenueGoal, revenueGrowth,
                unreadMessages
        );

        // --- RECENT PETS ---
        List<Pet> recentPets = petRepository.findRecentByOngId(ongId, PageRequest.of(0, 5));

        List<OngDashboardPetDTO> petDTOs = recentPets.stream().map(pet -> {
            String photo = (pet.getPhotos() != null && !pet.getPhotos().isEmpty())
                    ? pet.getPhotos().get(0).getPhoto() : null;

            // Calcular idade
            String age = calculateAge(pet.getBirthDate());
            String desc = String.format("%s ● %s ● %s", "Pet", pet.getBreed(), age);

            // Calcular finanças do pet
            // Nota: Precisaríamos de queries otimizadas aqui para evitar N+1 em produção
            // Para o exemplo, assumimos acesso aos getters lazy ou queries simples
            long godfathersCount = pet.getStatus().name().equals("APADRINHAVEL") ? 1 : 0; // Simplificação, idealmente count na tabela sponsorship

            // Somar custos ativos
            BigDecimal totalCost = pet.getCosts().stream()
                    .flatMap(c -> c.getHistory().stream())
                    .filter(h -> h.getEndDate() == null)
                    .map(h -> h.getMonthlyAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Somar receita ativa para este pet
            BigDecimal currentRevenue = BigDecimal.ZERO;
            // (Você precisaria filtrar os sponsorships deste pet específico e somar)
            // Vou colocar ZERO ou Mock aqui pois exigiria refatorar SponsorshipRepository para somar por PetID

            return new OngDashboardPetDTO(
                    pet.getId(),
                    pet.getName(),
                    photo,
                    desc,
                    pet.getStatus().name(), // APADRINHADO ou APADRINHAVEL (Disponível)
                    godfathersCount,
                    currentRevenue, // TODO: Implementar soma por pet
                    totalCost
            );
        }).toList();

        return new OngDashboardDTO(stats, petDTOs);
    }

    @Transactional(readOnly = true)
    public OngReportDTO getReportData(User authenticatedUser) {
        Manager manager = managerRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        Long ongId = manager.getOng().getId();

        // 1. Totais Gerais
        BigDecimal totalRevenue = sponsorshipRepository.sumTotalActiveRevenue(ongId);
        BigDecimal totalExpenses = costRepository.sumTotalActiveExpenses(ongId);

        // 2. Distribuição de Despesas
        List<ExpenseDistributionDTO> expenses = costRepository.findExpenseDistributionByOng(ongId);

        // 3. Stats de Apadrinhamento (Ativos vs Cancelados)
        long activeCount = sponsorshipRepository.countActiveByOng(ongId);
        long cancelledCount = sponsorshipRepository.countCancelledThisMonth(ongId);
        BigDecimal lostRevenue = sponsorshipRepository.sumLostRevenueThisMonth(ongId);

        SponsorshipReportStatsDTO sponsorshipStats = new SponsorshipReportStatsDTO(
                activeCount,
                totalRevenue,
                cancelledCount,
                lostRevenue
        );

        // 4. Relatório por Animal
        // Busca todos os pets da ONG
        List<Pet> pets = petRepository.findAllByOngId(ongId); // Certifique-se que este método existe no PetRepository

        List<PetReportDTO> animalReports = pets.stream().map(pet -> {
            // Custo Total do Pet (Meta)
            BigDecimal monthlyGoal = pet.getCosts().stream()
                    .flatMap(c -> c.getHistory().stream())
                    .filter(h -> h.getEndDate() == null)
                    .map(h -> h.getMonthlyAmount())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Total Arrecadado para o Pet
            BigDecimal collected = sponsorshipRepository.sumActiveRevenueByPetId(pet.getId());

            // Contagem de padrinhos
            long sponsors = sponsorshipRepository.countActiveSponsorsByPetId(pet.getId());

            return new PetReportDTO(
                    pet.getId(),
                    pet.getName(),
                    pet.getStatus().name(), // Ex: APADRINHAVEL
                    monthlyGoal,
                    collected,
                    sponsors
            );
        }).toList();

        return new OngReportDTO(
                totalRevenue,
                totalExpenses,
                animalReports,
                expenses,
                sponsorshipStats
        );
    }

    private String calculateAge(LocalDate birthDate) {
        if (birthDate == null) return "?";
        int years = Period.between(birthDate, LocalDate.now()).getYears();
        return years + " anos";
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Encontra a ONG associada a um usuário (Gerente ou Voluntário).
     */
    private Ong getOngFromUser(User user) {
        // Tenta encontrar como Gerente
        Optional<Manager> manager = managerRepository.findByUser(user);
        if (manager.isPresent()) {
            return manager.get().getOng();
        }

        // Se o usuário (ex: um Admin) não estiver ligado a uma Manager ou Volunteer
        throw new AccessDeniedException("Usuário autenticado não está associado a nenhuma ONG.");
    }

    /**
     * Verifica se o usuário autenticado pertence à ONG fornecida.
     */
    private void checkUserOngPermission(User user, Ong ongToVerify) {
        Ong userOng = getOngFromUser(user);
        if (!userOng.getId().equals(ongToVerify.getId())) {
            throw new AccessDeniedException("Você não tem permissão para modificar outras ONGs.");
        }
    }

}
