package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.VisitType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Doctor creates PENDING billing — services only (no discount/GST/medicine). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBillingRequest {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    @Size(max = 20)
    private String patientDisplayId;

    @Size(max = 50)
    private String patientCode;

    @NotBlank(message = "Patient name is required")
    @Size(max = 150)
    private String patientName;

    @Size(max = 20)
    private String contactNumber;

    @NotNull(message = "Billing date is required")
    private LocalDate billingDate;

    private VisitType visitType;

    @NotEmpty(message = "At least one service item is required")
    @Valid
    private List<BillingServiceItemRequest> services;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingServiceItemRequest {

        @Size(max = 100)
        private String serviceType;

        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal serviceFees;

        /** Package master id from mst_package (Package Type dropdown). */
        private UUID packageMasterId;

        @Size(max = 100)
        private String packageType;

        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal packageCharges;
    }

}
