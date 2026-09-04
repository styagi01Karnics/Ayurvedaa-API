package com.ayurveda.common.tenant;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts Bearer JWT claims into {@link TenantContext} and requires a hospital {@code schemaName}
 * for clinical APIs when configured.
 */
@Slf4j
@RequiredArgsConstructor
public class TenantSchemaFilter extends OncePerRequestFilter {

    public static final String TENANT_SCHEMA_HEADER = "X-Tenant-Schema";

    private static final String[] DEFAULT_SKIP_PATTERNS = {
            "/actuator/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/api-docs/**",
            "/v3/api-docs/**",
            "/error",
            "/iclock/**",
            "/cdata",
            "/cdata.aspx",
            "/getrequest",
            "/getrequest.aspx"
    };

    private final JwtClaimParser jwtClaimParser;
    private final TenantRoutingProperties routingProperties;
    private final ObjectMapper objectMapper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        for (String pattern : skipPatterns()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }

    private List<String> skipPatterns() {
        List<String> patterns = new ArrayList<>(List.of(DEFAULT_SKIP_PATTERNS));
        if (routingProperties.getSkipPathPatterns() != null) {
            patterns.addAll(routingProperties.getSkipPathPatterns());
        }
        return patterns;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, AppConstants.AUTHENTICATION_REQUIRED);
                return;
            }

            TenantClaims claims;
            try {
                claims = jwtClaimParser.parse(authorization.substring(7).trim());
            } catch (Exception ex) {
                log.warn("Invalid JWT for tenant routing: {}", ex.getMessage());
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, AppConstants.INVALID_OR_EXPIRED_TOKEN);
                return;
            }

            String schemaName = claims.getSchemaName();
            if (routingProperties.isRequireHospitalSchema() && !TenantSchemaNames.isHospitalSchema(schemaName)) {
                writeError(
                        response,
                        HttpServletResponse.SC_FORBIDDEN,
                        AppConstants.HOSPITAL_SCHEMA_REQUIRED);
                return;
            }

            if (schemaName != null && !schemaName.isBlank() && !TenantSchemaNames.isValidIdentifier(schemaName)) {
                writeError(response, HttpServletResponse.SC_BAD_REQUEST, AppConstants.INVALID_TENANT_SCHEMA);
                return;
            }

            String tenantCode = claims.getTenantCode();
            if (routingProperties.isRequireHospitalSchema()
                    && (tenantCode == null || tenantCode.isBlank())) {
                writeError(response, HttpServletResponse.SC_FORBIDDEN, AppConstants.TENANT_CODE_REQUIRED);
                return;
            }

            TenantContext.set(
                    claims.getTenantId(),
                    tenantCode,
                    schemaName,
                    claims.getUserId(),
                    claims.getRole(),
                    authorization);

            log.info(
                    "Tenant routing {} {} schema={} tenantCode={} role={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    schemaName,
                    tenantCode,
                    claims.getRole());

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(status, message));
    }

}
