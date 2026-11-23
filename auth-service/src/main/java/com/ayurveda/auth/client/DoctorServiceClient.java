package com.ayurveda.auth.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaFilter;

/**
 * Links a DOCTOR-profession user's code to an existing doctor record in doctor-service.
 * The caller's token and tenant schema are forwarded so doctor-service writes to the same tenant.
 */
@Component
public class DoctorServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String doctorServiceUrl;

    public DoctorServiceClient(@Value("${services.doctor.url}") String doctorServiceUrl) {
        this.doctorServiceUrl = doctorServiceUrl;
    }

    public void assignUserCodeToDoctor(UUID doctorId, String userCode) {
        Map<String, Object> body = Map.of("userCode", userCode);

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

        try {
            restTemplate.exchange(
                    doctorServiceUrl + "/api/v1/doctors/" + doctorId + "/user-code",
                    HttpMethod.PUT,
                    new HttpEntity<>(body, headers),
                    Object.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.CONFLICT) {
                throw new DuplicateResourceException("Doctor user code already exists: " + userCode);
            }
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Doctor not found: " + doctorId);
            }
            throw new BadRequestException("Doctor user code could not be linked: " + ex.getStatusText());
        } catch (RestClientException ex) {
            throw new BadRequestException("Doctor service is unavailable; user was not created");
        }
    }
}
