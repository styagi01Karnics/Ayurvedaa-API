package com.ayurveda.billing.service;

import java.util.UUID;

import com.ayurveda.billing.dto.response.PatientBillingResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.common.ApiResponse;

public interface PatientBillingService {

    /** Returns invoices, packages, and billing totals for a patient. */
    ApiResponse<PatientBillingResponse> getBillingByPatientId(
            UUID patientId, InvoiceStatus status);

}
