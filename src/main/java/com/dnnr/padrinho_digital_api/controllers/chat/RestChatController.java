package com.dnnr.padrinho_digital_api.controllers.chat;

import com.dnnr.padrinho_digital_api.dtos.chat.ChatMessageResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.chat.ConversationResponseDTO;
import com.dnnr.padrinho_digital_api.dtos.chat.InitiateChatContactDTO;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.services.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class RestChatController {

    private final ChatService chatService;

    /**
     * Retorna a "lista limpa" de contatos (Gerentes de ONGs)
     * com quem o Padrinho pode iniciar uma conversa.
     */
    @GetMapping("/initiate-list")
    @PreAuthorize("hasRole('PADRINHO')")
    public ResponseEntity<List<InitiateChatContactDTO>> getInitiateChatList(
            @AuthenticationPrincipal User user) {

        List<InitiateChatContactDTO> contacts = chatService.getInitiateChatList(user);
        return ResponseEntity.ok(contacts);
    }

    // Retorna a lista de conversas do usuário (sidebar)
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDTO>> getConversations(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getConversations(user.getId()));
    }

    // Retorna o histórico de mensagens paginado de uma conversa
    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<Page<ChatMessageResponseDTO>> getMessageHistory(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 30, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(chatService.getMessageHistory(user.getId(), otherUserId, pageable));
    }

    // Endpoint para marcar mensagens como LIDAS
    @PostMapping("/read/{otherUserId}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal User user) {
        chatService.markMessagesAsRead(user.getId(), otherUserId);
        return ResponseEntity.noContent().build();
    }
}