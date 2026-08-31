package com.ayurveda.billing.util;

import org.springframework.stereotype.Component;

import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.repository.InvoiceRepository;
import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InvoiceNumberGenerator {

    private final InvoiceRepository invoiceRepository;

    public String generate() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.INVOICE);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.INVOICE,
                invoiceRepository.findByInvoiceNumberStartingWith(prefix).stream()
                        .map(Invoice::getInvoiceNumber)
                        .toList());
    }

}
