package com.dnnr.padrinho_digital_api.controllers.volunteer;

import com.dnnr.padrinho_digital_api.dtos.volunteer.UpdateVolunteerDTO;
import com.dnnr.padrinho_digital_api.dtos.volunteer.VolunteerResponseDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.volunteer.VolunteerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("volunteer")
public class VolunteerController {

    @Autowired
    VolunteerService volunteerService;

    @GetMapping
    public ResponseEntity<Page<VolunteerResponseDTO>> getAllVolunteers(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            @AuthenticationPrincipal User authenticatedUser) {

        Page<VolunteerResponseDTO> page = volunteerService.getAllVolunteer(pageable, authenticatedUser);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerResponseDTO> getVolunteerById(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        VolunteerResponseDTO dto = volunteerService.getVolunteerById(id, authenticatedUser);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerResponseDTO> updateVolunteer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateVolunteerDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {

        VolunteerResponseDTO updatedDto = volunteerService.updateVolunteer(id, dto, authenticatedUser);
        return ResponseEntity.ok(updatedDto);
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<VolunteerResponseDTO> reactivateVolunteer(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        VolunteerResponseDTO updateDto = volunteerService.reactivateVolunteer(id, user);
        return ResponseEntity.ok(updateDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolunteer(
            @PathVariable Long id,
            @AuthenticationPrincipal User authenticatedUser) {

        volunteerService.deleteVolunteer(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
