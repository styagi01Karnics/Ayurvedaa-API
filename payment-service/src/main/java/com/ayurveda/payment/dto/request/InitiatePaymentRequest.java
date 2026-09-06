package com.ayurveda.payment.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
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
public class InitiatePaymentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", inclusive = true, message = "Amount must be at least 1.00")
    private BigDecimal amount;

    @Size(max = 100)
    private String productInfo;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    private String phone;

    private UUID invoiceId;

    private UUID patientId;

    @Size(max = 500)
    private String frontendSuccessUrl;

    @Size(max = 500)
    private String frontendFailureUrl;
}
