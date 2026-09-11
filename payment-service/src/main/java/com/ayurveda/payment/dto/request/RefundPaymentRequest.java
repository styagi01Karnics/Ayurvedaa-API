package com.ayurveda.payment.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundPaymentRequest {

    /**
     * Amount to refund. Omit to refund the full remaining (amount − already refunded).
     */
    @DecimalMin(value = "0.01", inclusive = true, message = "Refund amount must be greater than 0")
    private BigDecimal amount;

    @Size(max = 255)
    private String reason;
}
