package com.ayurveda.billing.kafka;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.common.kafka.PaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayuInvoiceRefundApplier {

    static final String PAYU_REFUND_REMARK_PREFIX = "PAYU_REFUND:";

    private final InvoiceService invoiceService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void apply(PaymentEvent event) {
        UUID invoiceId = event.getInvoiceId();
        if (invoiceId == null) {
            return;
        }
        BigDecimal amount = event.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        String token = StringUtils.hasText(event.getRefundRequestId())
                ? event.getRefundRequestId().trim()
                : (event.getPayuTxnId() != null ? event.getPayuTxnId() : event.getEventId());
        String remarks = PAYU_REFUND_REMARK_PREFIX + token;

        invoiceService.recordGatewayRefund(invoiceId, amount, "REFUND_PAYU", remarks);
        log.info(
                "Applied PayU refund to invoice {} amount={} token={}",
                invoiceId,
                amount,
                token);
    }
}
