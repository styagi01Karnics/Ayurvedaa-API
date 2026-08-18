package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    @NotBlank(message = "Patient name is required")
    @Size(max = 150)
    private String patientName;

    @Size(max = 20)
    private String contactNumber;

    @NotNull(message = "Billing date is required")
    private LocalDate billingDate;

    @NotEmpty(message = "At least one service item is required")
    @Valid
    private List<BillingServiceItemRequest> services;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillingServiceItemRequest {

        /** e.g. Consultation — required even when no package. */
        @NotBlank(message = "Service type is required")
        @Size(max = 100)
        private String serviceType;

        @NotNull(message = "Service fees are required")
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal serviceFees;

        /**
         * Optional. Null when patient comes for consultation/service only
         * (no package selected).
         */
        private UUID packageMasterId;

        @Size(max = 100)
        private String packageType;

        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal packageCharges;
    }

}
