package com.ayurveda.billing.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.dto.request.CreateBillingRequest;
import com.ayurveda.billing.dto.request.CreateInvoiceRequest;
import com.ayurveda.billing.dto.response.BillingListResponse;
import com.ayurveda.billing.dto.response.BillingResponse;
import com.ayurveda.billing.dto.response.InvoiceResponse;
import com.ayurveda.billing.enums.BillingStatus;
import com.ayurveda.common.ApiResponse;

public interface BillingService {

    /** Doctor creates PENDING billing (services only). */
    ApiResponse<BillingResponse> createBilling(CreateBillingRequest request);

    ApiResponse<BillingResponse> getBillingById(UUID billingId);

    ApiResponse<List<BillingListResponse>> getBillings(BillingStatus status);

    ApiResponse<List<BillingResponse>> getBillingsByPatientId(UUID patientId);

    /**
     * Receptionist creates invoice from PENDING billing.
     * Request body is the same as {@code POST /api/v1/invoices}
     * (can include medicines, therapies, discount, GST).
     */
    ApiResponse<InvoiceResponse> generateInvoice(UUID billingId, CreateInvoiceRequest request);

}
