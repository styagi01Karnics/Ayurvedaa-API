package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;

import com.ayurveda.billing.enums.PackageMasterStatus;

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
public class CreatePackageMasterRequest {

    @NotBlank(message = "Package name is required")
    @Size(max = 150)
    private String name;

    @NotNull(message = "Package price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Package price cannot be negative")
    private BigDecimal packagePrice;

    private PackageMasterStatus status;

}
