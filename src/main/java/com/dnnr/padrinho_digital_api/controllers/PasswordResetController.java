package com.dnnr.padrinho_digital_api.controllers;

import com.dnnr.padrinho_digital_api.dtos.users.ForgotPasswordDTO;
import com.dnnr.padrinho_digital_api.dtos.users.ResetPasswordDTO;
import com.dnnr.padrinho_digital_api.services.auth.PasswordResetService;
import jakarta.mail.SendFailedException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Endpoint para solicitar a redefinição de senha.
     * Recebe o email.
     */
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgotPasswordDTO dto) throws SendFailedException {
        passwordResetService.requestPasswordReset(dto.email());
        return ResponseEntity.ok("Se o email estiver cadastrado, um link de redefinição será enviado.");
    }

    /**
     * Endpoint para validar o token (quando o usuário acessa a página do front).
     * Recebe o token como query param.
     */
    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok("Token válido.");
    }

    /**
     * Endpoint para efetivamente redefinir a senha.
     * Recebe o token e a nova senha.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordDTO dto) {
        passwordResetService.resetPassword(dto.token(), dto.newPassword());
        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }
}