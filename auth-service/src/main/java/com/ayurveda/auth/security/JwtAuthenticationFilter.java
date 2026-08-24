package com.ayurveda.auth.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                try {
                    String token = header.substring(7);
                    AuthPrincipal principal = jwtService.parseToken(token);

                    List<GrantedAuthority> authorities = new ArrayList<>();
                    if (principal.getRole() != null && !principal.getRole().isBlank()) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + principal.getRole()));
                    }
                    if (principal.getPageCodes() != null) {
                        for (String pageCode : principal.getPageCodes()) {
                            if (pageCode != null && !pageCode.isBlank()) {
                                authorities.add(new SimpleGrantedAuthority("PAGE_" + pageCode.trim().toUpperCase()));
                            }
                        }
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    TenantContext.set(
                            principal.getTenantId(),
                            principal.getTenantCode(),
                            principal.getSchemaName());
                } catch (Exception ex) {
                    log.warn("Invalid JWT token: {}", ex.getMessage());
                    SecurityContextHolder.clearContext();
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

}
