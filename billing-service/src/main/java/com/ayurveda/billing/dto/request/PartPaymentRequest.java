package com.ayurveda.billing.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
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
public class PartPaymentRequest {

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Payment amount must be greater than 0")
    private BigDecimal amountPaid;

    @Size(max = 50)
    private String paymentMethod;

    @Size(max = 255)
    private String remarks;

}
