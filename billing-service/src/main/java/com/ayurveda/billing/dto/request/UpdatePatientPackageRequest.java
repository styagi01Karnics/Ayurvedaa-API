package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

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
public class UpdatePatientPackageRequest {

    @NotBlank(message = "Package name is required")
    @Size(max = 150)
    private String packageName;

    @NotNull(message = "Validity is required")
    private LocalDate validity;

    @NotNull(message = "Discount applied is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Discount cannot be negative")
    private BigDecimal discountApplied;

}
