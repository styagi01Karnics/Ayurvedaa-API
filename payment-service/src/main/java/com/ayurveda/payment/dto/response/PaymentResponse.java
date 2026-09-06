package com.ayurveda.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.ayurveda.payment.enums.PaymentGateway;
import com.ayurveda.payment.enums.PaymentStatus;
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
public class PaymentResponse {

    private UUID id;
    private String paymentCode;
    private String payuTxnId;
    private BigDecimal amount;
    private String productInfo;
    private String firstName;
    private String email;
    private String phone;
    private UUID invoiceId;
    private UUID patientId;
    private PaymentGateway gateway;
    private PaymentStatus status;
    private String payuStatus;
    private String mihpayid;
    private String paymentMode;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** PayU hosted checkout POST URL. Present only on initiate. */
    private String actionUrl;

    /** Form fields to POST to PayU. Present only on initiate. */
    @Builder.Default
    private Map<String, String> payuParams = new LinkedHashMap<>();
}
