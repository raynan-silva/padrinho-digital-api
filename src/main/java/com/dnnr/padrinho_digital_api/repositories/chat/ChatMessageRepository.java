package com.dnnr.padrinho_digital_api.repositories.chat;

import com.dnnr.padrinho_digital_api.entities.chat.ChatMessage;
import com.dnnr.padrinho_digital_api.entities.chat.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // Busca o histórico de mensagens de uma conversa, paginado
    Page<ChatMessage> findByConversationIdOrderByCreatedAtDesc(Long conversationId, Pageable pageable);

    // Busca a última mensagem de uma conversa (para a sidebar)
    Optional<ChatMessage> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);

    // Conta mensagens não lidas para um usuário em uma conversa
    long countByConversationIdAndReceiverIdAndStatusNot(Long conversationId, Long receiverId, MessageStatus status);

    // Marca mensagens como LIDAS
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = 'LIDA' WHERE m.conversation.id = :conversationId AND m.receiver.id = :receiverId AND m.status <> 'LIDA'")
    void markMessagesAsRead(@Param("conversationId") Long conversationId, @Param("receiverId") Long receiverId);

    // 5. Contar mensagens recebidas (Total)
    // Nota: O filtro 'receiver.id' usa o ID do User, não do Godfather
    long countByReceiverId(Long userId);

    // Contar mensagens não lidas onde o destinatário é o Usuário (Gerente) da ONG
    @Query("SELECT COUNT(m) FROM ChatMessage m " +
            "WHERE m.receiver.id = :userId AND m.status != 'LIDA'")
    long countUnreadMessages(@Param("userId") Long userId);
}
