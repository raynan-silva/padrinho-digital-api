package com.dnnr.padrinho_digital_api.services.volunteer;

import com.dnnr.padrinho_digital_api.dtos.volunteer.UpdateVolunteerDTO;
import com.dnnr.padrinho_digital_api.dtos.volunteer.VolunteerResponseDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.users.*;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import com.dnnr.padrinho_digital_api.repositories.users.VolunteerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VolunteerService {
    @Autowired
    VolunteerRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ManagerRepository managerRepository;

    @Transactional(readOnly = true)
    public Page<VolunteerResponseDTO> getAllVolunteer(Pageable pageable, User authenticatedUser) {
        if (authenticatedUser.getRole() == Role.ADMIN) {
            // Regra Admin: Retorna todos
            Page<Volunteer> volunteerPage = repository.findAll(pageable);
            return volunteerPage.map(VolunteerResponseDTO::new);

        } else if (authenticatedUser.getRole() == Role.GERENTE) {
            // Regra Gerente: Retorna apenas da sua ONG
            Ong userOng = getOngFromUser(authenticatedUser);
            Page<Volunteer> volunteerPage = repository.findAllByOngId(userOng.getId(), pageable);
            return volunteerPage.map(VolunteerResponseDTO::new);

        } else {
            // Outras roles (Padrinho, etc.) não podem listar
            throw new AccessDeniedException("Você não tem permissão para listar voluntários.");
        }
    }

    /**
     * READ (By ID)
     * Busca um voluntário com base na role.
     */
    @Transactional(readOnly = true)
    public VolunteerResponseDTO getVolunteerById(Long id, User authenticatedUser) {
        Volunteer volunteer = findVolunteerByIdAndCheckPermission(id, authenticatedUser, "visualizar");
        return new VolunteerResponseDTO(volunteer);
    }

    /**
     * UPDATE
     * Gerente edita um voluntário (nome/foto) da sua ONG.
     * Admin não pode editar (conforme regra 2).
     */
    @Transactional
    public VolunteerResponseDTO updateVolunteer(Long id, UpdateVolunteerDTO dto, User authenticatedUser) {
        if (authenticatedUser.getRole() != Role.GERENTE) {
            throw new AccessDeniedException("Apenas Gerentes podem editar voluntários.");
        }

        // A checagem de permissão (se o voluntário é da ONG do gerente)
        // é feita dentro deste método auxiliar.
        Volunteer volunteer = findVolunteerByIdAndCheckPermission(id, authenticatedUser, "editar");

        User userToUpdate = volunteer.getUser();
        userToUpdate.setName(dto.name());
        userToUpdate.setPhoto(dto.photo());
        // Não salvamos o 'repository' (Volunteer), mas sim o 'userRepository'
        userRepository.save(userToUpdate);

        return new VolunteerResponseDTO(volunteer);
    }

    /**
     * PATCH
     * Gerente reativa um funcionário.
     * Admin não pode reativar.
     */
    @Transactional
    public VolunteerResponseDTO reactivateVolunteer(Long id, User authenticatedUser) {
        if (authenticatedUser.getRole() != Role.GERENTE) {
            throw new AccessDeniedException("Apenas Gerentes podem reativasr voluntários.");
        }

        // A checagem de permissão (se o voluntário é da ONG do gerente)
        // é feita dentro deste método auxiliar.
        Volunteer volunteer = findVolunteerByIdAndCheckPermission(id, authenticatedUser, "editar");

        User userToUpdate = volunteer.getUser();
        userToUpdate.setStatus(UserStatus.ATIVO);

        userRepository.save(userToUpdate);

        return new VolunteerResponseDTO(volunteer);
    }

    /**
     * DELETE (Lógico)
     * Admin ou Gerente (da sua ONG) podem desativar um voluntário.
     */
    @Transactional
    public void deactivateVolunteer(Long id, User authenticatedUser) {
        Volunteer volunteer = findVolunteerByIdAndCheckPermission(id, authenticatedUser, "excluir");

        User userToDeactivate = volunteer.getUser();
        userToDeactivate.setStatus(UserStatus.INATIVO); // Exclusão Lógica
        userRepository.save(userToDeactivate);
    }

    // --- MÉTODOS AUXILIARES ---

    /**
     * Método auxiliar para buscar um voluntário e checar a permissão.
     */
    private Volunteer findVolunteerByIdAndCheckPermission(Long id, User user, String action) {
        Optional<Volunteer> volunteerOpt;

        if (user.getRole() == Role.ADMIN) {
            volunteerOpt = repository.findById(id);

        } else if (user.getRole() == Role.GERENTE) {
            Ong userOng = getOngFromUser(user);
            volunteerOpt = repository.findByIdAndOngId(id, userOng.getId());

        } else {
            throw new AccessDeniedException("Você não tem permissão para " + action + " este voluntário.");
        }

        return volunteerOpt.orElseThrow(() ->
                new EntityNotFoundException("Voluntário não encontrado ou você não tem permissão para " + action + " (ID: " + id + ")")
        );
    }

    /**
     * Seu método auxiliar para encontrar a ONG do usuário (Gerente ou Voluntário).
     */
    public Ong getOngFromUser(User user) {
        Optional<Manager> manager = managerRepository.findByUser(user);
        if (manager.isPresent()) {
            return manager.get().getOng();
        }

        Optional<Volunteer> volunteer = repository.findByUser(user);
        if (volunteer.isPresent()) {
            return volunteer.get().getOng();
        }

        throw new AccessDeniedException("Usuário autenticado não está associado a nenhuma ONG.");
    }
}
