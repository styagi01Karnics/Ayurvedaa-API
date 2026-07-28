package com.ayurveda.billing.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.common.ApiResponse;

public interface InvoiceService {

    ApiResponse<InvoiceResponse> createInvoice(CreateInvoiceRequest request);

    ApiResponse<InvoiceResponse> getInvoiceById(UUID invoiceId);

    ApiResponse<List<InvoiceListResponse>> getInvoices(String patientId, InvoiceStatus status);

    ApiResponse<InvoiceResponse> recordPartPayment(UUID invoiceId, PartPaymentRequest request);

    ApiResponse<Void> deleteInvoice(UUID invoiceId);

}
