package com.dnnr.padrinho_digital_api.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${app.frontend.reset-url}")
    private String frontendResetUrl;

    @Value("${app.frontend.login-url}")
    private String frontendLoginUrl;

    // Use @Async para não bloquear a thread da requisição
    @Async
    public void sendPasswordResetEmail(String to, String token, String userName) throws SendFailedException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            // 1. Monta a URL de reset
            String resetUrl = frontendResetUrl + "?token=" + token;
            String loginUrl = frontendLoginUrl;

            // 2. Prepara as variáveis para o template
            Context context = new Context();
            context.setVariable("userName", userName);
            context.setVariable("resetUrl", resetUrl);
            context.setVariable("loginUrl", loginUrl);

            // 3. Processa o template Thymeleaf
            String htmlContent = templateEngine.process("password-reset-template", context);

            // 4. Configura e envia o email
            helper.setTo(to);
            helper.setSubject("Padrinho Digital - Redefinição de Senha");
            helper.setText(htmlContent, true); // true indica que é HTML
            helper.setFrom("nao-responda@padrinhodigital.com"); // Opcional: "De"

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new SendFailedException("Erro ao enviar email de recuperação de senha");
        }
    }
}
