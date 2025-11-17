package com.dnnr.padrinho_digital_api.dtos.chat;

import java.time.LocalDateTime;

public record ConversationResponseDTO(
        Long conversationId,
        Long otherUserId,
        String otherUserName,
        String otherUserPhoto, // URL da foto
        String lastMessage,
        LocalDateTime lastMessageTimestamp,
        long unreadCount
) {}
