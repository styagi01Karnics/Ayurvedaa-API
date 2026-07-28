package com.ayurveda.billing.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.repository.InvoiceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceNumberGenerator {

    private final InvoiceRepository invoiceRepository;

    public String generate() {
        Optional<Invoice> latest = invoiceRepository.findTopByOrderByInvoiceNumberDesc();
        if (latest.isEmpty() || latest.get().getInvoiceNumber() == null) {
            return "INV-1001";
        }

        String last = latest.get().getInvoiceNumber();
        int number = Integer.parseInt(last.replace("INV-", ""));
        return String.format("INV-%d", number + 1);
    }

}
