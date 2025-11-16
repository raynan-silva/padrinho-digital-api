package com.dnnr.padrinho_digital_api.services.godfather;

import com.dnnr.padrinho_digital_api.dtos.godfather.GodfatherProfileStatsDTO;
import com.dnnr.padrinho_digital_api.dtos.user.ChangePasswordDTO;
import com.dnnr.padrinho_digital_api.dtos.user.UpdateUserDTO;
import com.dnnr.padrinho_digital_api.dtos.users.ProfileResponseDTO;
import com.dnnr.padrinho_digital_api.entities.godfather.GodfatherLevel;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipStatus;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.UserStatus;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.repositories.godfather.GodfatherSealRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor // Substitui @Autowired por injeção de construtor
public class GodfatherService {
    private final GodfatherRepository godfatherRepository;
    private final GodfatherSealRepository godfatherSealRepository;
    private final SponsorshipHistoryRepository sponsorshipHistoryRepository;

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
}
