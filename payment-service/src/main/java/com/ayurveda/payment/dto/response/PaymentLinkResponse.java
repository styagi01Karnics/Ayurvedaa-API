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
    /** Patient web pay page (email / fallback). */
    private String payUrl;
    /**
     * Encode as on-screen QR.
     * When {@code upiQr=true}: {@code upi://pay?...&am=...} (scan → UPI with exact amount).
     * Otherwise same as payUrl.
     */
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
    /** True when {@code qrPayload} is a UPI intent string (scan → pay exact amount). */
    private boolean upiQr;
}
