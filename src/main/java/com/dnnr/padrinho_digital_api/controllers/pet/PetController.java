package com.dnnr.padrinho_digital_api.controllers.pet;

import com.dnnr.padrinho_digital_api.dtos.pet.CreatePetDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.PetResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.pet.UpdatePetDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.pet.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pet")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public ResponseEntity<PetResponseDTO> createPet(@RequestBody @Valid CreatePetDTO data,
                                                    @AuthenticationPrincipal User authenticatedUser) {
        PetResponseDTO newPet = petService.createPet(data, authenticatedUser);
        // Retorna 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(newPet);
    }

    /**
     * READ (Paginated)
     * Ex: GET /pet?page=0&size=10&sort=name,asc
     */
    @GetMapping
    public ResponseEntity<Page<PetResponseDTO>> getAllPets(
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {
        Page<PetResponseDTO> petPage = petService.getAllPets(pageable);
        return ResponseEntity.ok(petPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable Long id) {
        PetResponseDTO pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> updatePet(@PathVariable Long id,
                                                    @RequestBody @Valid UpdatePetDTO data,
                                                    @AuthenticationPrincipal User authenticatedUser) {
        PetResponseDTO updatedPet = petService.updatePet(id, data, authenticatedUser);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id,
                                          @AuthenticationPrincipal User authenticatedUser) {
        petService.deletePet(id, authenticatedUser);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}
