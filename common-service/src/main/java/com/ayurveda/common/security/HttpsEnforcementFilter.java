package com.ayurveda.common.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Blocks cleartext HTTP when {@code ayurveda.security.require-https=true}.
 * Honours {@code X-Forwarded-Proto} for reverse-proxy TLS termination.
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
@RequiredArgsConstructor
public class HttpsEnforcementFilter extends OncePerRequestFilter {

    private static final Set<String> SKIP_PATTERNS = Set.of(
            "/actuator/health",
            "/actuator/health/**",
            "/actuator/info",
            "/actuator/prometheus");

    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        for (String pattern : SKIP_PATTERNS) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (isHttps(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiResponse.failure(HttpServletResponse.SC_FORBIDDEN, AppConstants.HTTPS_REQUIRED));
    }

    static boolean isHttps(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwarded = request.getHeader("X-Forwarded-Proto");
        if (forwarded != null) {
            for (String part : forwarded.split(",")) {
                if ("https".equalsIgnoreCase(part.trim())) {
                    return true;
                }
            }
        }
        return false;
    }
}
