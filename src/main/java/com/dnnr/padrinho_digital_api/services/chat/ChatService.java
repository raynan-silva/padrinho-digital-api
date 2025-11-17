package com.dnnr.padrinho_digital_api.services.chat;

import com.dnnr.padrinho_digital_api.dtos.chat.*;
import com.dnnr.padrinho_digital_api.entities.chat.ChatConversation;
import com.dnnr.padrinho_digital_api.entities.chat.ChatMessage;
import com.dnnr.padrinho_digital_api.entities.chat.MessageStatus;
import com.dnnr.padrinho_digital_api.entities.chat.TypeContent;
import com.dnnr.padrinho_digital_api.entities.ong.Ong;
import com.dnnr.padrinho_digital_api.entities.sponsorship.SponsorshipHistory;
import com.dnnr.padrinho_digital_api.entities.users.Godfather;
import com.dnnr.padrinho_digital_api.entities.users.Manager;
import com.dnnr.padrinho_digital_api.entities.users.User;
import com.dnnr.padrinho_digital_api.exceptions.ResourceNotFoundException;
import com.dnnr.padrinho_digital_api.repositories.chat.ChatConversationRepository;
import com.dnnr.padrinho_digital_api.repositories.chat.ChatMessageRepository;
import com.dnnr.padrinho_digital_api.repositories.sponsorship.SponsorshipHistoryRepository;
import com.dnnr.padrinho_digital_api.repositories.users.GodfatherRepository;
import com.dnnr.padrinho_digital_api.repositories.users.ManagerRepository;
import com.dnnr.padrinho_digital_api.repositories.users.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate; // Para enviar updates de 'LIDA'
    private final GodfatherRepository godfatherRepository;
    private final SponsorshipHistoryRepository sponsorshipHistoryRepository;
    private final ManagerRepository managerRepository;

    /**
     * Retorna a "lista limpa" de contatos que o Padrinho PODE iniciar.
     * Regra: Apenas ONGs de apadrinhamentos ATIVOS e que ele
     * ainda NÃO tenha uma conversa.
     */
    @Transactional(readOnly = true)
    public List<InitiateChatContactDTO> getInitiateChatList(User authenticatedUser) {

        // 1. Encontra o Padrinho
        Godfather godfather = godfatherRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new EntityNotFoundException("Perfil de Padrinho não encontrado."));

        // 2. Busca a lista de conversas JÁ EXISTENTES
        // (Reaproveita o método que você já tem)
        List<ConversationResponseDTO> existingConversations = this.getConversations(authenticatedUser.getId());
        Set<Long> existingPartnerIds = existingConversations.stream()
                .map(ConversationResponseDTO::otherUserId)
                .collect(Collectors.toSet());

        // 3. Busca apadrinhamentos ativos e extrai as ONGs únicas
        List<SponsorshipHistory> activeSponsorships = sponsorshipHistoryRepository
                .findAllActiveSponsorshipsByGodfatherId(godfather.getId());

        Set<Ong> sponsoredOngs = activeSponsorships.stream()
                .map(sh -> sh.getSponsorship().getPet().getOng())
                .collect(Collectors.toSet());

        // 4. Monta a "lista limpa"
        List<InitiateChatContactDTO> potentialContacts = new ArrayList<>();

        for (Ong ong : sponsoredOngs) {
            // Encontra o Gerente (representante) da ONG
            Optional<Manager> managerOpt = managerRepository.findManagerWithUserByOngId(ong.getId());

            if (managerOpt.isPresent()) {
                User managerUser = managerOpt.get().getUser();

                // ** A MÁGICA **
                // Se o Padrinho ainda não tem um chat com este Gerente,
                // adiciona ele na lista de "novos chats".
                if (!existingPartnerIds.contains(managerUser.getId())) {
                    potentialContacts.add(new InitiateChatContactDTO(
                            ong.getId(),
                            ong.getName(),
                            managerUser.getId(),
                            managerUser.getName(),
                            managerUser.getPhoto()
                    ));
                }
            }
            // Se a ONG não tiver gerente, ela simplesmente não aparece na lista (seguro).
        }

        return potentialContacts;
    }

    @Transactional
    public ChatMessage saveMessage(ChatSendDTO dto, User sender) {
        User receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Destinatário não encontrado"));

        // Encontra ou cria a conversa
        ChatConversation conversation = getOrCreateConversation(sender, receiver);

        // Cria e salva a entidade da mensagem
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(dto.content());
        message.setTypeContent(TypeContent.valueOf(dto.typeContent()));
        message.setStatus(MessageStatus.ENVIADA); // Padrão

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversations(Long userId) {
        // 1. Busca todas as conversas do usuário
        List<ChatConversation> conversations = conversationRepository.findAllByUserId(userId);

        // 2. Mapeia para o DTO de resposta
        return conversations.stream().map(convo -> {
            // 3. Descobre quem é o "outro" usuário
            User otherUser = convo.getUserA().getId().equals(userId) ? convo.getUserB() : convo.getUserA();

            // 4. Busca a última mensagem (pode ser otimizado)
            ChatMessage lastMessage = messageRepository.findTopByConversationIdOrderByCreatedAtDesc(convo.getId())
                    .orElse(null); // Pode não ter mensagens

            // 5. Conta mensagens não lidas
            long unreadCount = messageRepository.countByConversationIdAndReceiverIdAndStatusNot(
                    convo.getId(), userId, MessageStatus.LIDA);

            return new ConversationResponseDTO(
                    convo.getId(),
                    otherUser.getId(),
                    otherUser.getName(),
                    otherUser.getPhoto(),
                    lastMessage != null ? lastMessage.getContent() : "Sem mensagens...",
                    lastMessage != null ? lastMessage.getCreatedAt() : convo.getCreatedAt(),
                    unreadCount
            );
        }).collect(Collectors.toList());
    }

    @Transactional()
    public Page<ChatMessageResponseDTO> getMessageHistory(Long userId, Long otherUserId, Pageable pageable) {
        ChatConversation conversation = getOrCreateConversation(
                userRepository.findById(userId).get(),
                userRepository.findById(otherUserId).get());

        Page<ChatMessage> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversation.getId(), pageable);
        return messages.map(ChatMessageResponseDTO::new);
    }

    @Transactional
    public void markMessagesAsRead(Long userId, Long otherUserId) {
        User user = userRepository.findById(userId).get();
        User otherUser = userRepository.findById(otherUserId).get();

        ChatConversation conversation = getOrCreateConversation(user, otherUser);

        // Marca as mensagens no banco
        messageRepository.markMessagesAsRead(conversation.getId(), userId);

        // Notifica o "outro usuário" (o sender) via WebSocket que as mensagens foram lidas
        messagingTemplate.convertAndSendToUser(
                otherUser.getEmail(), // O "username" (email) dele
                "/queue/status", // O destino
                new ChatMessageStatusUpdateDTO(conversation.getId(), MessageStatus.LIDA)
        );
    }


    // --- Método Helper Chave ---
    @Transactional
    public ChatConversation getOrCreateConversation(User userA, User userB) {
        // Lógica para garantir A-B == B-A
        // Sempre salva o usuário com ID menor como userA
        System.out.println(userA.getName());
        System.out.println(userA.getId());
        System.out.println(userB.getId());
        User uA = userA.getId() < userB.getId() ? userA : userB;
        User uB = userA.getId() < userB.getId() ? userB : userA;

        return conversationRepository.findConversationByUsers(uA.getId(), uB.getId())
                .orElseGet(() -> {
                    ChatConversation newConversation = new ChatConversation();
                    newConversation.setUserA(uA);
                    newConversation.setUserB(uB);
                    return conversationRepository.save(newConversation);
                });
    }
}