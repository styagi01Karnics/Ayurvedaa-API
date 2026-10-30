package com.ayurveda.doctor.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.doctor.dto.client.AuthUserClientRequest;
import com.ayurveda.doctor.dto.client.AuthUserClientResponse;
import com.ayurveda.doctor.dto.client.TenantRoleClientResponse;

@FeignClient(name = "auth-service", url = "${services.auth.url}")
public interface AuthServiceClient {

    @GetMapping("/api/v1/roles")
    ApiResponse<List<TenantRoleClientResponse>> listRoles();

    @PostMapping("/api/v1/auth/register-user")
    ApiResponse<AuthUserClientResponse> registerUser(@RequestBody AuthUserClientRequest request);

    @DeleteMapping("/api/v1/auth/users/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable("userId") UUID userId);

}
