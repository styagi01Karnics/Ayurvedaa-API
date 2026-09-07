package com.ayurveda.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentLinkResponse {

    private UUID id;
    private String token;
    /** Patient pay page. Encode this as the POS QR. */
    private String payUrl;
    /** Same as payUrl — POS / QR clients can render this string. */
    private String qrPayload;
    private UUID invoiceId;
    private String invoiceNumber;
    private UUID patientId;
    private BigDecimal amount;
    private String firstName;
    private String email;
    private String phone;
    private LocalDateTime expiresAt;
    private String status;
    private boolean emailSent;
}
