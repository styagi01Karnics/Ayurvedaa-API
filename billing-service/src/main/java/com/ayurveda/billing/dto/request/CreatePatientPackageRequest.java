package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.billing.enums.PackageStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class CreatePatientPackageRequest {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    @NotBlank(message = "Package name is required")
    @Size(max = 150)
    private String packageName;

    @NotNull(message = "Validity is required")
    private LocalDate validity;

    /** Optional; defaults to SCHEDULED. */
    private PackageStatus status;

    @NotNull(message = "Discount applied is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative")
    private BigDecimal discountApplied;

}
