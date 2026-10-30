package com.ayurveda.notification.websocket;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.ayurveda.common.tenant.JwtClaimParser;
import com.ayurveda.common.tenant.TenantClaims;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Validates the JWT on the WebSocket handshake. Browsers cannot set headers on WebSocket
 * connections, so the token is read from the {@code token} query parameter, with the
 * {@code Authorization: Bearer} header as a fallback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationHandshakeInterceptor implements HandshakeInterceptor {

    public static final String USER_ID_ATTRIBUTE = "NOTIFICATION_USER_ID";

    private final JwtClaimParser jwtClaimParser;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) {

        String token = resolveToken(request);
        if (token == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        try {
            TenantClaims claims = jwtClaimParser.parse(token);
            attributes.put(USER_ID_ATTRIBUTE, claims.getUserId().toString());
            return true;
        } catch (Exception ex) {
            log.warn("WebSocket handshake rejected: invalid token. {}", ex.getMessage());
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }

    private String resolveToken(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String queryToken = servletRequest.getServletRequest().getParameter("token");
            if (queryToken != null && !queryToken.isBlank()) {
                return queryToken;
            }
        }
        String header = request.getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ") && header.length() > 7) {
            return header.substring(7);
        }
        return null;
    }
}
