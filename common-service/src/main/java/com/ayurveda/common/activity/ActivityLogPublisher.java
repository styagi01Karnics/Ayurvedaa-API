package com.ayurveda.common.activity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Publishes activity logs to activity-log-service.
 * Failures are logged and never break the calling business flow.
 * Forwards Bearer / schema headers from {@link TenantContext} when present.
 */
@Slf4j
@Component
public class ActivityLogPublisher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.activity-log.url:http://localhost:8107}")
    private String activityLogBaseUrl;

    @Value("${services.activity-log.enabled:true}")
    private boolean enabled;

    public void record(String page, ActivityActionType action, String target) {
        record(page, action, target, null, null, null, null, null);
    }

    public void record(
            String page,
            ActivityActionType action,
            String target,
            String beforeValue,
            String afterValue) {
        record(page, action, target, beforeValue, afterValue, null, null, null);
    }

    public void record(
            String page,
            ActivityActionType action,
            String target,
            String beforeValue,
            String afterValue,
            UUID performedByUserId,
            String performedByUserName,
            String performedByRole) {

        if (!enabled || page == null || action == null || target == null || target.isBlank()) {
            return;
        }

        try {
            CreateActivityLogClientRequest body = CreateActivityLogClientRequest.builder()
                    .page(page)
                    .action(action)
                    .target(target)
                    .beforeValue(beforeValue)
                    .afterValue(afterValue)
                    .activityTimestamp(LocalDateTime.now())
                    .performedByUserId(performedByUserId)
                    .performedByUserName(performedByUserName)
                    .performedByRole(performedByRole)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String authorization = TenantContext.getAuthorizationHeader();
            if (authorization != null && !authorization.isBlank()) {
                headers.set(HttpHeaders.AUTHORIZATION, authorization);
            }
            String schema = TenantContext.getSchemaName();
            if (schema != null && !schema.isBlank()) {
                headers.set(TenantSchemaFilter.TENANT_SCHEMA_HEADER, schema);
            }

            restTemplate.postForEntity(
                    activityLogBaseUrl + "/api/v1/activity-logs",
                    new HttpEntity<>(body, headers),
                    Object.class);

            log.debug("Activity log published: {} {} {}", page, action, target);
        } catch (Exception ex) {
            log.warn("Failed to publish activity log [{} {} {}]: {}",
                    page, action, target, ex.getMessage());
        }
    }

}
