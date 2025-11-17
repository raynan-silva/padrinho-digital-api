package com.dnnr.padrinho_digital_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Define os prefixos do "broker" (para onde o Spring envia)
        // O cliente vai se inscrever em "/user/queue/messages"
        registry.enableSimpleBroker("/queue", "/topic");

        // Define o prefixo do "aplicativo" (para onde o cliente envia)
        // O cliente vai enviar mensagens para "/app/chat.send"
        registry.setApplicationDestinationPrefixes("/app");

        // Habilita o roteamento de mensagens para usuários específicos
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Define o endpoint de conexão WebSocket que o frontend usará
        // "/ws" é a URL de conexão (ex: ws://localhost:8080/ws)
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS(); // SockJS é um fallback para navegadores antigos
    }
}