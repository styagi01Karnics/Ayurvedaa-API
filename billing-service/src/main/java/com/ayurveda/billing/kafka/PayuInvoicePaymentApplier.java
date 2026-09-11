package com.ayurveda.billing.kafka;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.entity.InvoicePayment;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.repository.InvoiceRepository;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.billing.util.InvoiceCalculationUtil;
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

        if (invoice.getStatus() == InvoiceStatus.COMPLETED) {
            log.info("Invoice {} already COMPLETED; skipping PayU txnid={}", invoiceId, event.getPayuTxnId());
            return;
        }

        String remark = PAYU_REMARK_PREFIX + event.getPayuTxnId();
        boolean alreadyApplied = invoice.getPayments().stream()
                .map(InvoicePayment::getRemarks)
                .anyMatch(value -> value != null && value.equals(remark));
        if (alreadyApplied) {
            log.info("PayU txnid={} already applied to invoice {}", event.getPayuTxnId(), invoiceId);
            return;
        }

        BigDecimal amount = event.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal left = InvoiceCalculationUtil.leftAmount(invoice.getTotalAmount(), invoice.getPaidAmount());
        if (left.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Invoice {} has no left amount; skipping PayU txnid={}", invoiceId, event.getPayuTxnId());
            return;
        }

        // Never over-apply if PayU amount exceeds remaining due (e.g. race / rounding).
        BigDecimal applyAmount = amount.min(left);

        invoiceService.recordPartPayment(invoiceId, PartPaymentRequest.builder()
                .amountPaid(applyAmount)
                .paymentMethod("PAYU")
                .remarks(remark)
                .build());
        log.info(
                "Recorded PayU payment {} on invoice {} amount={} leftWas={}",
                event.getPayuTxnId(),
                invoiceId,
                applyAmount,
                left);
    }
}
