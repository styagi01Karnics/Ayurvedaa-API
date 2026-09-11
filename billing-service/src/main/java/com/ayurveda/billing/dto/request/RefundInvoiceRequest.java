package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
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
public class RefundInvoiceRequest {

    /**
     * Amount to refund. Omit to refund the full paid balance (cash in-hand return).
     */
    @DecimalMin(value = "0.01", inclusive = true, message = "Refund amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255)
    private String reason;

    /** Patient email for refund confirmation (hospital mailbox SMTP). */
    @Email
    @Size(max = 150)
    private String patientEmail;
}
