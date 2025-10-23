package com.dnnr.padrinho_digital_api.services.pet;

import com.dnnr.padrinho_digital_api.dtos.pet.CreatePetDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.PetResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.UpdatePetDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.pet.PetGender;
import com.dnnr.padrinho_digital_api.entities.pet.PetStatus;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.Volunteer;
import com.dnnr.padrinho_digital_api.exceptions.PetNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.pet.PetRepository;
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
public class PetService {
    @Autowired
    PetRepository repository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    VolunteerRepository volunteerRepository;

    /**
     * CREATE
     * Cria um novo pet e o associa à ONG do usuário autenticado.
     */
    @Transactional
    public PetResponseDTO createPet(CreatePetDTO data, User authenticatedUser){

        Ong ong = getOngFromUser(authenticatedUser);

        Pet pet = new Pet(data.name(), data.birth_date(), PetStatus.APADRINHAVEL, data.breed(), data.weight(),
                data.date_of_admission(), data.gender(), data.profile(), ong);

        Pet savedPet = repository.save(pet);

        return new PetResponseDTO(savedPet);
    }

    /**
     * READ (Paginated)
     * Retorna todos os pets de forma paginada.
     */
    @Transactional(readOnly = true)
    public Page<PetResponseDTO> getAllPets(Pageable pageable) {
        // Busca a página de entidades
        Page<Pet> petPage = repository.findAll(pageable);
        // Mapeia a página de entidades para uma página de DTOs
        return petPage.map(PetResponseDTO::new);
    }

    /**
     * READ (By ID)
     * Retorna um pet específico pelo ID.
     */
    @Transactional(readOnly = true)
    public PetResponseDTO getPetById(Long id) {
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet com ID " + id + " não encontrado."));
        return new PetResponseDTO(pet);
    }

    /**
     * UPDATE (Partial)
     * Atualiza um pet. Só permite se o usuário for da mesma ONG do pet.
     */
    @Transactional
    public PetResponseDTO updatePet(Long id, UpdatePetDTO data, User authenticatedUser) {
        // 1. Busca o pet
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet com ID " + id + " não encontrado."));

        // 2. Validação de segurança: O usuário pertence à ONG deste pet?
        checkUserOngPermission(authenticatedUser, pet.getOng());

        // 3. Lógica de atualização parcial (só atualiza o que não for nulo)
        if (data.name() != null) pet.setName(data.name());
        if (data.birth_date() != null) pet.setBirthDate(data.birth_date());
        if (data.status() != null) pet.setStatus(data.status());
        if (data.breed() != null) pet.setBreed(data.breed());
        if (data.weight() != null) pet.setWeight(data.weight());
        if (data.date_of_admission() != null) pet.setDateOfAdmission(data.date_of_admission());
        if (data.gender() != null) pet.setGender(data.gender());
        if (data.profile() != null) pet.setProfile(data.profile());

        Pet updatedPet = repository.save(pet);
        return new PetResponseDTO(updatedPet);
    }

    /**
     * DELETE (Logical)
     * Realiza a exclusão lógica mudando o status para INDISPONIVEL.
     */
    @Transactional
    public void deletePet(Long id, User authenticatedUser) {
        // 1. Busca o pet
        Pet pet = repository.findById(id)
                .orElseThrow(() -> new PetNotFoundException("Pet com ID " + id + " não encontrado."));

        // 2. Validação de segurança
        checkUserOngPermission(authenticatedUser, pet.getOng());

        // 3. Exclusão Lógica
        pet.setStatus(PetStatus.INDISPONIVEL);
        repository.save(pet);
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
