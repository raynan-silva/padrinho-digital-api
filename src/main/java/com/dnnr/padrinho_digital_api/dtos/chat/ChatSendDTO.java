package com.dnnr.padrinho_digital_api.dtos.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChatSendDTO(
        @NotNull(message = "O ID do destinatário é obrigatório.")
        Long receiverId,

        @NotBlank(message = "O conteúdo não pode ser vazio.")
        String content,

        @NotNull(message = "O tipo de conteúdo é obrigatório.")
        String typeContent // "TEXTO" ou "IMAGEM"
) {}