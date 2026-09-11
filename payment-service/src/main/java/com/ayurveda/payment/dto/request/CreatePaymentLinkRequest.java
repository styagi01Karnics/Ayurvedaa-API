package com.ayurveda.payment.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentLinkRequest {

    @NotNull(message = "Invoice id is required")
    private UUID invoiceId;

    private UUID patientId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", inclusive = true, message = "Amount must be at least 1.00")
    private BigDecimal amount;

    @Size(max = 80)
    private String invoiceNumber;

    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Email is required")
    @Email
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 20)
    private String phone;

    /** Browser origin used to build /pay/{token}, e.g. http://localhost:5173 */
    @Size(max = 300)
    private String payPageBaseUrl;

    /** When true, emails the pay URL to {@code email} (use after a partial payment). */
    private Boolean sendEmail;

    /**
     * When true, calls PayU Dynamic QR (DBQR) and sets {@code qrPayload} to
     * {@code upi://pay?...&am=...} so scanning opens UPI with the exact amount.
     * {@code payUrl} remains the web fallback / email link.
     */
    private Boolean upiQr;
}
