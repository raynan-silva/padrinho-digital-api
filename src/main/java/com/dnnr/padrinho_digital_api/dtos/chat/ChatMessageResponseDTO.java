package com.dnnr.padrinho_digital_api.dtos.chat;

import com.dnnr.padrinho_digital_api.entities.chat.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageResponseDTO(
        Long id,
        Long conversationId,
        Long senderId,
        Long receiverId,
        String content,
        String typeContent,
        String status,
        LocalDateTime createdAt
) {
    public ChatMessageResponseDTO(ChatMessage entity) {
        this(
                entity.getId(),
                entity.getConversation().getId(),
                entity.getSender().getId(),
                entity.getReceiver().getId(),
                entity.getContent(),
                entity.getTypeContent().name(),
                entity.getStatus().name(),
                entity.getCreatedAt()
        );
    }
}
