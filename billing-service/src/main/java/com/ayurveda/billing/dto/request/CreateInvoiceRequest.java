package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.VisitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
public class CreateInvoiceRequest {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    @NotBlank(message = "Patient name is required")
    @Size(max = 150)
    private String patientName;

    @Size(max = 20)
    private String contactNumber;

    @NotNull(message = "Invoice date is required")
    private LocalDate invoiceDate;

    private VisitType visitType;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal serviceFees;

    /**
     * Optional. Null when consultation/service only (no package).
     * Same as billing service item packageMasterId.
     */
    private UUID packageMasterId;

    @Size(max = 100)
    private String packageType;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal packageCharges;

    @Valid
    private List<MedicineItemRequest> medicines;

    @Valid
    private List<TherapyItemRequest> therapies;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal discount;

    private Boolean taxEnabled;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal cgstPercent;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal sgstPercent;

    /** Optional first payment when generating the invoice (supports part payment). */
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal amountPaid;

    @Size(max = 50)
    private String paymentMethod;

    @Size(max = 255)
    private String paymentRemarks;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedicineItemRequest {

        /** Selected from medicine dropdown. */
        @NotNull(message = "Medicine id is required")
        private UUID medicineId;

        @NotNull
        @Min(1)
        private Integer quantity;

        /** Optional override; if omitted, selling price is taken from medicine inventory. */
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal unitPrice;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TherapyItemRequest {

        @NotBlank
        @Size(max = 150)
        private String itemName;

        @NotNull
        @Min(1)
        private Integer quantity;

        @NotNull
        @DecimalMin(value = "0.0", inclusive = true)
        private BigDecimal unitPrice;

        private UUID assignedTherapistId;

        @Size(max = 150)
        private String assignedTherapistName;

        private LocalDate scheduleDate;

        private LocalTime scheduleTime;

        private Integer sessionDuration;

        private Integer sessionFrequency;
    }

}
