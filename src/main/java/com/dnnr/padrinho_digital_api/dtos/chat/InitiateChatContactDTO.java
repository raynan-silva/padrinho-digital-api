package com.dnnr.padrinho_digital_api.dtos.chat;

public record InitiateChatContactDTO(
        Long ongId,
        String ongName,
        Long managerUserId, // O ID do Usuário (do Gerente) para iniciar o chat
        String managerName,
        String managerPhoto
) {}
