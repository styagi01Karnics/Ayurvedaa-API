package com.ayurveda.notification.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.ayurveda.notification.websocket.NotificationHandshakeHandler;
import com.ayurveda.notification.websocket.NotificationHandshakeInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * STOMP endpoint for real-time notifications. Clients connect to /ws?token=JWT and
 * subscribe to /user/queue/notifications. The server pushes via SimpMessagingTemplate.
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class NotificationWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final NotificationHandshakeInterceptor handshakeInterceptor;
    private final NotificationHandshakeHandler handshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(handshakeInterceptor)
                .setHandshakeHandler(handshakeHandler)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue");
        registry.setUserDestinationPrefix("/user");
    }
}
