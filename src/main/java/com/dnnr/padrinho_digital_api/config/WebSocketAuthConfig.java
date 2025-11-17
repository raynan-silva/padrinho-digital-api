package com.dnnr.padrinho_digital_api.config;

import com.dnnr.padrinho_digital_api.infra.security.TokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99) // Prioridade alta
public class WebSocketAuthConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService; // O UserDetailsService do Spring Security

    public WebSocketAuthConfig(TokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 1. Verifica se é um frame de CONEXÃO
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    // 2. Pega o token da header "Authorization"
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("Header: " + authHeader);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwt = authHeader.substring(7);

                        String email = tokenService.validateToken(jwt);
                        System.out.println("Email | validateToken : " + email);

                        // 3. Valida o token
                        if (email != null && !email.isEmpty()) {
                            // 4. Pega o email (username) do token

                            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                            // 5. Autentica o usuário na sessão do WebSocket
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.setUser(authentication);
                        }
                    } else {
                        throw new BadCredentialsException("Token JWT inválido no WebSocket.");
                    }
                }
                return message;
            }
        });
    }
}