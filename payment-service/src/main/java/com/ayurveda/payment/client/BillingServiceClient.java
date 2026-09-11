package com.ayurveda.payment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.payment.dto.client.InvoiceClientResponse;

@FeignClient(name = "billing-service", url = "${services.billing.url}")
public interface BillingServiceClient {

    @GetMapping("/api/v1/invoices/{invoiceId}")
    ApiResponse<InvoiceClientResponse> getInvoiceById(@PathVariable("invoiceId") UUID invoiceId);
}
