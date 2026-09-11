package com.ayurveda.billing.service;

import java.util.UUID;

import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.request.PartPaymentRequest;
import com.ayurveda.billing.dto.request.RefundInvoiceRequest;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

public interface InvoiceService {

    /** Creates a new invoice and deducts medicine stock when applicable. */
    ApiResponse<InvoiceResponse> createInvoice(CreateInvoiceRequest request);

    /** Returns a single active invoice by ID, including items and payments. */
    ApiResponse<InvoiceResponse> getInvoiceById(UUID invoiceId);

    /** Lists invoices filtered by patient (UUID or search text) and optional status. */
    ApiResponse<PagedResponse<InvoiceListResponse>> getInvoices(
            String patientId, InvoiceStatus status, int page, int size);

    /** Lists invoices for a patient UUID, with optional status filter. */
    ApiResponse<PagedResponse<InvoiceListResponse>> getInvoicesByPatientId(
            UUID patientId, InvoiceStatus status, int page, int size);

    /** Records a part payment against an unpaid or partially paid invoice. */
    ApiResponse<InvoiceResponse> recordPartPayment(UUID invoiceId, PartPaymentRequest request);

    /** Cash in-hand refund: reduces paid amount and recalculates left / status. */
    ApiResponse<InvoiceResponse> recordCashRefund(UUID invoiceId, RefundInvoiceRequest request);

    /**
     * Applies a PayU refund from payment-service (Kafka). Idempotent on remarks key.
     * {@code paymentMethod} should be {@code REFUND_PAYU}.
     */
    ApiResponse<InvoiceResponse> recordGatewayRefund(
            UUID invoiceId,
            java.math.BigDecimal amount,
            String paymentMethod,
            String remarks);

    /** Soft-deletes an invoice and restores deducted medicine stock. */
    ApiResponse<Void> deleteInvoice(UUID invoiceId);

}
