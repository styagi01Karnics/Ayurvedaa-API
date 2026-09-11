package com.ayurveda.billing.service;

import java.util.UUID;

import com.ayurveda.billing.dto.request.CreateBillingRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.response.BillingListResponse;
import com.ayurveda.billing.dto.response.BillingResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.BillingStatus;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

public interface BillingService {

    /** Doctor creates PENDING billing (services only). */
    ApiResponse<BillingResponse> createBilling(CreateBillingRequest request);

    ApiResponse<BillingResponse> getBillingById(UUID billingId);

    ApiResponse<PagedResponse<BillingListResponse>> getBillings(
            BillingStatus status, int page, int size);

    ApiResponse<PagedResponse<BillingResponse>> getBillingsByPatientId(
            UUID patientId, int page, int size);

    /**
     * Receptionist creates invoice from PENDING billing.
     * Request body is the same as {@code POST /api/v1/invoices}
     * (can include medicines, therapies, discount, GST).
     */
    ApiResponse<InvoiceResponse> generateInvoice(UUID billingId, CreateInvoiceRequest request);

}
