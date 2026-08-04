package com.ayurveda.billing.dto.request;

import com.ayurveda.billing.enums.PackageStatus;

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
public class UpdatePatientPackageStatusRequest {

    @NotNull(message = "Status is required")
    private PackageStatus status;

}
