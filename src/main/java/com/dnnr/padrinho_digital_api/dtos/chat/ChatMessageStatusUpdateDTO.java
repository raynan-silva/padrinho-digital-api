package com.dnnr.padrinho_digital_api.dtos.chat;

import com.dnnr.padrinho_digital_api.entities.chat.MessageStatus;

public record ChatMessageStatusUpdateDTO(
        Long conversationId,
        MessageStatus status
) {
}
