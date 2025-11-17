package com.dnnr.padrinho_digital_api.repositories.chat;

import com.dnnr.padrinho_digital_api.entities.chat.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    /**
     * Encontra uma conversa entre dois usuários, independentemente de quem é A ou B.
     * Esta é a lógica que garante que (A, B) é o mesmo que (B, A).
     */
    @Query("SELECT c FROM ChatConversation c WHERE (c.userA.id = :userAId AND c.userB.id = :userBId) OR (c.userA.id = :userBId AND c.userB.id = :userAId)")
    Optional<ChatConversation> findConversationByUsers(@Param("userAId") Long userAId, @Param("userBId") Long userBId);

    /**
     * Encontra todas as conversas de um usuário específico.
     */
    @Query("SELECT c FROM ChatConversation c LEFT JOIN FETCH c.userA LEFT JOIN FETCH c.userB WHERE c.userA.id = :userId OR c.userB.id = :userId")
    List<ChatConversation> findAllByUserId(@Param("userId") Long userId);
}
