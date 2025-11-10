package com.dnnr.padrinho_digital_api.services.history;

import com.dnnr.padrinho_digital_api.dtos.history.ContributionHistoryDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.Volunteer;
import com.dnnr.padrinho_digital_api.repositories.projection.ContributionHistoryProjection;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContributionHistoryService {

    private final com.dnnr.padrinho_digital_api.repositories.history.ContributionHistoryRepository historyRepository;
    // Supondo que você tenha helpers para obter a ONG ou Padrinho do usuário
    private final GodfatherRepository godfatherRepository;
    private final ManagerRepository managerRepository;
    private final VolunteerRepository volunteerRepository;

    @Transactional(readOnly = true)
    public Page<ContributionHistoryDTO> getHistory(User authenticatedUser, Pageable pageable) {

        Page<ContributionHistoryProjection> projectionPage;

        switch (authenticatedUser.getRole()) {
            case PADRINHO:
                // Regra Padrinho: Busca o ID do padrinho associado ao usuário
                Long godfatherId = getGodfatherFromUser(authenticatedUser).getId();
                projectionPage = historyRepository.findHistoryForGodfather(godfatherId, pageable);
                break;

            case VOLUNTARIO:
            case GERENTE:
                // Regra ONG: Busca o ID da ONG associada ao usuário
                Long ongId = getOngFromUser(authenticatedUser).getId();
                projectionPage = historyRepository.findHistoryForOng(ongId, pageable);
                break;

            case ADMIN:
            default:
                // Regra Admin: Busca tudo
                projectionPage = historyRepository.findHistoryForAdmin(pageable);
                break;
        }

        // Mapeia a Projeção (do SQL) para o DTO (do Frontend)
        return projectionPage.map(ContributionHistoryDTO::new);
    }

    private Godfather getGodfatherFromUser(User user) {
        return godfatherRepository.findByUser(user)
                .orElseThrow(() -> new AccessDeniedException("Usuário autenticado não é um Padrinho."));
    }

    /**
     * Encontra a ONG associada a um usuário (Gerente ou Voluntário).
     */
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

    /**
     * Verifica se o usuário autenticado pertence à ONG fornecida.
     */
    private void checkUserOngPermission(User user, Ong ongToVerify) {
        Ong userOng = getOngFromUser(user);
        if (!userOng.getId().equals(ongToVerify.getId())) {
            throw new AccessDeniedException("Você não tem permissão para modificar pets de outra ONG.");
        }
    }
}
