package com.dnnr.padrinho_digital_api.services.pet;

import com.dnnr.padrinho_digital_api.dtos.pet.CreatePetDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.PetResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.UpdatePetDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.pet.Pet;
import com.dnnr.padrinho_digital_api.entities.pet.PetGender;
import com.dnnr.padrinho_digital_api.entities.pet.PetStatus;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.entities.users.Volunteer;
import com.dnnr.padrinho_digital_api.exceptions.PetNotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.pet.PetRepository;
import com.dnnr.padrinho_digital_api.repositories.photo.PhotoRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.VolunteerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {
    @Autowired
    PetRepository repository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    VolunteerRepository volunteerRepository;

    @Autowired
    PhotoRepository photoRepository;

    /**
     * CREATE
     * Cria um novo pet e o associa à ONG do usuário autenticado.
     */
    @Transactional
    public PetResponseDTO createPet(CreatePetDTO data, User authenticatedUser){
        System.out.println("AQUIIUIUIU");
        Ong ong = getOngFromUser(authenticatedUser);

        Pet pet = new Pet(data.name(), data.birth_date(), PetStatus.APADRINHAVEL, data.breed(), data.weight(),
                data.date_of_admission(), data.gender(), data.profile(), ong);

        if (data.photos() != null && !data.photos().isEmpty()) {
            List<Photo> photoList = data.photos().stream()
                    .map(Photo::new)
                    .collect(Collectors.toList());
            pet.setPhotos(photoList);
        }

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
        Pet pet = repository.findByIdWithPhotos(id)
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

    @Transactional
    public PetResponseDTO addPhotos(Long petId, List<String> base64Photos, User authenticatedUser) {
        Pet pet = repository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet com ID " + petId + " não encontrado"));

        checkUserOngPermission(authenticatedUser, pet.getOng());

        List<Photo> newPhotos = base64Photos.stream()
                .map(Photo::new)
                .toList();

        pet.getPhotos().addAll(newPhotos);

        repository.save(pet);

        return getPetById(petId);
    }

    @Transactional
    public void removePhoto(Long petId, Long photoId, User authenticatedUser) {
        Pet pet = repository.findById(petId)
                .orElseThrow(() -> new PetNotFoundException("Pet com ID " + petId + " não encontrado"));

        checkUserOngPermission(authenticatedUser, pet.getOng());

        Photo photoToRemove = pet.getPhotos().stream()
                .filter(p -> p.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Foto com ID " + photoId + " não encontrda."));

        pet.getPhotos().remove(photoToRemove);
        repository.save(pet);

        photoRepository.delete(photoToRemove);
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
