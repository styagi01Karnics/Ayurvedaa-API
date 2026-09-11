package com.ayurveda.common.kafka;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
public class PaymentEvent {

    public static final String TYPE_UPDATED = "PAYMENT_UPDATED";

    private String eventId;
    private String eventType;
    private Instant occurredAt;

    private UUID tenantId;
    private String tenantCode;
    private String schemaName;

    private UUID paymentId;
    private String paymentCode;
    private String payuTxnId;
    private UUID invoiceId;
    private UUID patientId;
    private UUID initiatedByUserId;

    private BigDecimal amount;
    private String status;
    private String paymentMode;
    private String mihpayid;
    private String firstName;
    private String email;
    /** Present for refund events — PayU request_id or merchant refund token. */
    private String refundRequestId;
}
