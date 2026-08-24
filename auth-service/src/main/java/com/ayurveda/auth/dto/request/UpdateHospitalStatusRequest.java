package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.enums.TenantStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHospitalStatusRequest {

    @NotNull(message = "Status is required")
    private TenantStatus status;

}
