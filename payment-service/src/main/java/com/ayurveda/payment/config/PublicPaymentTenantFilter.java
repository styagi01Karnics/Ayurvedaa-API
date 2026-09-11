package com.ayurveda.payment.config;

import java.io.IOException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaNames;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Public pay-link routes skip JWT tenant routing. Bind {@code hosp_*} from the
 * token prefix before Hibernate opens a persistence session.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 15)
public class PublicPaymentTenantFilter extends OncePerRequestFilter {

    static final String PUBLIC_PREFIX = "/api/v1/payments/public/";

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith(PUBLIC_PREFIX);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String schema = schemaFromToken(tokenFromPath(request.getRequestURI()));
        if (TenantSchemaNames.isHospitalSchema(schema)) {
            TenantContext.set(null, null, schema);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    static String tokenFromPath(String path) {
        if (path == null || !path.startsWith(PUBLIC_PREFIX)) {
            return "";
        }
        String rest = path.substring(PUBLIC_PREFIX.length());
        int slash = rest.indexOf('/');
        return slash < 0 ? rest : rest.substring(0, slash);
    }

    static String schemaFromToken(String token) {
        if (token == null || !token.contains(".")) {
            return "";
        }
        return token.substring(0, token.indexOf('.'));
    }
}
