package com.ayurveda.common.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * Forwards the inbound Bearer token (and schema header) on Feign calls between clinical services.
 */
@Component
@ConditionalOnClass(RequestInterceptor.class)
@ConditionalOnProperty(prefix = "ayurveda.tenant.routing", name = "enabled", havingValue = "true")
public class TenantFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        String authorization = TenantContext.getAuthorizationHeader();
        if (authorization != null && !authorization.isBlank()) {
            template.header(org.springframework.http.HttpHeaders.AUTHORIZATION, authorization);
        }
        String schema = TenantContext.getSchemaName();
        if (schema != null && !schema.isBlank()) {
            template.header(TenantSchemaFilter.TENANT_SCHEMA_HEADER, schema);
        }
    }

}
