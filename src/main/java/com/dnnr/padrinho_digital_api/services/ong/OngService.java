package com.dnnr.padrinho_digital_api.services.ong;

import com.dnnr.padrinho_digital_api.dtos.ong.OngResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.ong.UpdateOngDTO;
import com.dnnr.padrinho_digital_api.entities.ong.Address;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.ong.OngStatus;
import com.dnnr.padrinho_digital_api.entities.photo.Photo;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.NotFoundException;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.ong.AddressRepository;
import com.dnnr.padrinho_digital_api.repositories.ong.OngRepository;
import com.dnnr.padrinho_digital_api.repositories.photo.PhotoRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OngService {
    @Autowired
    OngRepository repository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    PhotoRepository photoRepository;

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
