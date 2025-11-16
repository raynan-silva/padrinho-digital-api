package com.dnnr.padrinho_digital_api.controllers.user;

import com.dnnr.padrinho_digital_api.dtos.user.ChangePasswordDTO;
import com.dnnr.padrinho_digital_api.dtos.user.UpdateUserDTO;
import com.dnnr.padrinho_digital_api.dtos.users.ProfileResponseDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para atualizar o perfil (nome/email).
     * O {id} é o ID da entidade User
     */
    @PutMapping()
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @Valid @RequestBody UpdateUserDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {

        ProfileResponseDTO updatedUser = userService.update(dto, authenticatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Endpoint para alterar a senha.
     * Não precisa de ID, pois opera no usuário logado.
     */
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordDTO dto,
            @AuthenticationPrincipal User authenticatedUser) {

        userService.changePassword(dto, authenticatedUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<Void> deactivateUser(
            @AuthenticationPrincipal User authenticatedUser
    ) {
        userService.deactivateUser(authenticatedUser);
        return ResponseEntity.noContent().build();
    }

}
