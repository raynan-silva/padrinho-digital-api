package com.dnnr.padrinho_digital_api.controllers.chat;

import com.dnnr.padrinho_digital_api.dtos.chat.ChatMessageResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.chat.ChatSendDTO;
import com.dnnr.padrinho_digital_api.entities.chat.ChatMessage;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import com.dnnr.padrinho_digital_api.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private  final UserRepository userRepository;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatSendDTO dto,
                            Principal principal) {

        System.out.println(principal);
        System.out.println(principal.getName());
        // 1. Salva a mensagem no banco
        User sender = (User) userRepository.findByEmail(principal.getName());

        System.out.println(sender);

        ChatMessage savedMessage = chatService.saveMessage(dto, sender);
        ChatMessageResponseDTO responseDTO = new ChatMessageResponseDTO(savedMessage);

        // 2. Envia a mensagem para o destinatário
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiver().getEmail(), // O "username" (email) do destinatário
                "/queue/messages", // O destino
                responseDTO
        );

        // 3. (Opcional) Envia uma cópia de "enviado" de volta para o remetente
        // Isso é útil se o remetente tiver múltiplas abas abertas
        messagingTemplate.convertAndSendToUser(
                sender.getEmail(),
                "/queue/messages",
                responseDTO
        );
    }
}