package com.ayurveda.notification.websocket;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * Makes the authenticated user ID the STOMP principal so that
 * {@code convertAndSendToUser(userId, ...)} routes to that user's sessions.
 */
@Component
public class NotificationHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        Object userId = attributes.get(NotificationHandshakeInterceptor.USER_ID_ATTRIBUTE);
        if (userId == null) {
            return null;
        }
        String name = userId.toString();
        return () -> name;
    }
}
