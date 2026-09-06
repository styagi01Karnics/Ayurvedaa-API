package com.ayurveda.billing.kafka;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.entity.InvoicePayment;
import com.ayurveda.billing.repository.InvoiceRepository;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.common.kafka.PaymentEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayuInvoicePaymentApplier {

    static final String PAYU_REMARK_PREFIX = "PAYU:";

    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void apply(PaymentEvent event) {
        UUID invoiceId = event.getInvoiceId();
        Invoice invoice = invoiceRepository.findByIdAndDeletedFalse(invoiceId).orElse(null);
        if (invoice == null) {
            log.warn("Invoice {} not found for PayU txnid={}", invoiceId, event.getPayuTxnId());
            return;
        }
        invoice.getPayments().size();
        String remark = PAYU_REMARK_PREFIX + event.getPayuTxnId();
        boolean alreadyApplied = invoice.getPayments().stream()
                .map(InvoicePayment::getRemarks)
                .anyMatch(value -> value != null && value.equals(remark));
        if (alreadyApplied) {
            log.info("PayU txnid={} already applied to invoice {}", event.getPayuTxnId(), invoiceId);
            return;
        }

        BigDecimal amount = event.getAmount();
        if (amount == null) {
            return;
        }
        invoiceService.recordPartPayment(invoiceId, PartPaymentRequest.builder()
                .amountPaid(amount)
                .paymentMethod("PAYU")
                .remarks(remark)
                .build());
        log.info("Recorded PayU payment {} on invoice {}", event.getPayuTxnId(), invoiceId);
    }
}
