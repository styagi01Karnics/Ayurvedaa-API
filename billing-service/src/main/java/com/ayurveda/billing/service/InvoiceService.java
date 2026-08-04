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

    /** Creates a new invoice and deducts medicine stock when applicable. */
    ApiResponse<InvoiceResponse> createInvoice(CreateInvoiceRequest request);

    /** Returns a single active invoice by ID, including items and payments. */
    ApiResponse<InvoiceResponse> getInvoiceById(UUID invoiceId);

    /** Lists invoices filtered by patient (UUID or search text) and optional status. */
    ApiResponse<List<InvoiceListResponse>> getInvoices(String patientId, InvoiceStatus status);

    /** Records a part payment against an unpaid or partially paid invoice. */
    ApiResponse<InvoiceResponse> recordPartPayment(UUID invoiceId, PartPaymentRequest request);

    /** Soft-deletes an invoice and restores deducted medicine stock. */
    ApiResponse<Void> deleteInvoice(UUID invoiceId);

}
