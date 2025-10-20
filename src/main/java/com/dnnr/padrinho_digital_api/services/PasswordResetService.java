package com.dnnr.padrinho_digital_api.services;

import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.ExpiredTokenException;
import com.dnnr.padrinho_digital_api.exceptions.InvalidTokenException;
import com.dnnr.padrinho_digital_api.exceptions.UserNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Tempo de expiração em minutos
    private static final int EXPIRATION_MINUTES = 30;

    /**
     * Passo 1: Usuário pede a redefinição
     */
    @Transactional
    public void requestPasswordReset(String email) {
        // Valida se o usuário existe
        // NOTA: findByEmail deve retornar User, não UserDetails, ajuste seu repo
        User user = (User) userRepository.findByEmail(email);
        if (user == null) {
            // Lançar exceção ou retornar silenciosamente?
            // Lançar é mais fácil de debugar, mas retornar silenciosamente
            // previne "enumeração de usuários". Vamos lançar por clareza.
            throw new UserNotFoundException("Usuário não encontrado com o email: " + email);
        }

        // Gera o token
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        // Salva o token e a expiração no usuário
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(expiryDate);
        userRepository.save(user);

        // Envia o email (assíncrono)
        emailService.sendPasswordResetEmail(user.getEmail(), token, user.getName());
    }

    /**
     * Passo 2: O Front chama para validar o token (quando o usuário carrega a página)
     */
    @Transactional(readOnly = true)
    public void validateToken(String token) {
        validateTokenInternal(token); // Reusa a lógica de validação
    }

    /**
     * Passo 3: Usuário envia a nova senha
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Valida o token e busca o usuário
        User user = validateTokenInternal(token);

        // Atualiza a senha
        user.setPassword(passwordEncoder.encode(newPassword));

        // Invalida o token para não ser usado novamente
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);

        userRepository.save(user);
    }

    /**
     * Lógica interna de validação de token
     */
    private User validateTokenInternal(String token) {
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token inválido ou não encontrado."));

        // Verifica se o token expirou
        if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ExpiredTokenException("Seu token de redefinição de senha expirou.");
        }

        return user;
    }
}
