package com.dnnr.padrinho_digital_api.services.godfather;

import com.dnnr.padrinho_digital_api.dtos.godfather.DashboardStatsDTO;
import com.dnnr.padrinho_digital_api.dtos.godfather.GodfatherDashboardDTO;
import com.dnnr.padrinho_digital_api.dtos.godfather.GodfatherProfileStatsDTO;
import com.dnnr.padrinho_digital_api.dtos.godfather.RecentSponsorshipDTO;
import com.dnnr.padrinho_digital_api.dtos.user.ChangePasswordDTO;
import com.dnnr.padrinho_digital_api.dtos.user.UpdateUserDTO;
import com.dnnr.padrinho_digital_api.dtos.users.ProfileResponseDTO;
import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.sponsorship.Sponsorship;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.UserStatus;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.repositories.chat.ChatMessageRepository;
import com.dnnr.padrinho_digital_api.repositories.donation_campaign.DonationRepository;
import com.dnnr.padrinho_digital_api.repositories.godfather.GodfatherSealRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@RequiredArgsConstructor // Substitui @Autowired por injeção de construtor
public class GodfatherService {
    private final GodfatherRepository godfatherRepository;
    private final GodfatherSealRepository godfatherSealRepository;
    private final SponsorshipHistoryRepository sponsorshipHistoryRepository;
    private final SponsorshipRepository sponsorshipRepository;
    private final DonationRepository donationRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public GodfatherProfileStatsDTO getGodfatherStats(User authenticatedUser) {
        // 1. Encontra o Padrinho
        Godfather godfather = godfatherRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de Padrinho não encontrado."));

        // 2. Busca o nível (que já está no objeto Godfather)
        GodfatherLevel level = godfather.getCurrentLevel();
        if (level == null) {
            // Fallback, isso não deve acontecer se o cadastro estiver correto
            throw new EntityNotFoundException("Nível do Padrinho não configurado.");
        }

        // 3. Conta apadrinhamentos ativos
        long activeSponsorships = sponsorshipHistoryRepository
                .countBySponsorshipGodfatherIdAndStatus(godfather.getId(), SponsorshipStatus.ATIVO);

        // 4. Conta selos conquistados
        long sealsCount = godfatherSealRepository.countByGodfatherId(godfather.getId());

        // 5. Retorna o DTO
        return new GodfatherProfileStatsDTO(
                activeSponsorships,
                sealsCount,
                level.getName(),
                level.getIconUrl()
        );
    }

    @Transactional(readOnly = true)
    public GodfatherDashboardDTO getDashboardData(User authenticatedUser) {
        // 1. Identificar Padrinho
        Godfather godfather = godfatherRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new EntityNotFoundException("Padrinho não encontrado"));

        // --- ESTATÍSTICAS (CARDS) ---

        // Card 1: Animais Apadrinhados (Ativos)
        long totalActive = sponsorshipRepository.countActiveSponsorships(godfather.getId());

        // Card 2: Total Investido (Soma das Doações em Campanhas + Estimativa ou apenas Doações)
        // Nota: Como não temos tabela de "Pagamentos de Mensalidade", somarei apenas Doações de Campanha
        // Se quiser somar mensalidades, precisaria de uma lógica mais complexa ou tabela de transações.
        BigDecimal totalInvested = donationRepository.sumTotalDonationsByGodfather(godfather.getId());

        // Cálculo da Média (Total / Animais)
        BigDecimal average = BigDecimal.ZERO;
        if (totalActive > 0) {
            average = totalInvested.divide(BigDecimal.valueOf(totalActive), 2, RoundingMode.HALF_UP);
        }

        // Card 3: Participação em Campanhas
        long totalCampaigns = donationRepository.countDistinctCampaignsByGodfather(godfather.getId());

        // Card 4: Mensagens Recebidas (Usa o ID do User)
        long totalMessages = chatMessageRepository.countByReceiverId(authenticatedUser.getId());

        DashboardStatsDTO stats = new DashboardStatsDTO(
                totalActive,
                totalInvested,
                average,
                totalCampaigns,
                totalMessages
        );

        // --- LISTA DE APADRINHAMENTOS RECENTES ---

        // Pega apenas os 5 mais recentes
        List<Sponsorship> recentEntities = sponsorshipRepository.findRecentByGodfather(
                godfather.getId(),
                PageRequest.of(0, 5)
        );

        List<RecentSponsorshipDTO> recentList = recentEntities.stream().map(s -> {
            Pet pet = s.getPet();
            String photo = (pet.getPhotos() != null && !pet.getPhotos().isEmpty())
                    ? pet.getPhotos().get(0).getPhoto()
                    : null;

            // Lógica simples para "Próxima Doação": Dia 15 do mês seguinte (exemplo)
            // Você pode ajustar isso para a data de vencimento real se tiver
            LocalDate nextDonation = LocalDate.now().plusMonths(1).withDayOfMonth(15);

            return new RecentSponsorshipDTO(
                    s.getId(),
                    pet.getId(),
                    pet.getName(),
                    photo,
                    "Pet ● " + pet.getBreed(), // Ex: "Cão ● Vira-lata"
                    calculateAge(pet.getBirthDate()),
                    nextDonation
            );
        }).toList();

        return new GodfatherDashboardDTO(stats, recentList);
    }

    private String calculateAge(LocalDate birthDate) {
        if (birthDate == null) return "? anos";
        int years = Period.between(birthDate, LocalDate.now()).getYears();
        if (years == 0) {
            return Period.between(birthDate, LocalDate.now()).getMonths() + " meses";
        }
        return years + " anos";
    }
}
