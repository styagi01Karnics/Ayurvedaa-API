package com.ayurveda.billing.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.billing.dto.response.PatientBillingResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.service.PatientBillingService;
import com.ayurveda.common.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Patient Billing", description = "Patient-wise billing aggregate APIs")
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Validated
public class PatientBillingController {

    private final PatientBillingService patientBillingService;

    @Operation(
            summary = "Get billing data by patient id",
            description = """
                    Returns invoices, patient packages, and payment summary totals for the patient.
                    Optional invoice status filter: UNPAID, ONGOING, COMPLETED.
                    """)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PatientBillingResponse>> getBillingByPatientId(
            @PathVariable UUID patientId,
            @RequestParam(required = false) InvoiceStatus status) {

        return ResponseEntity.ok(patientBillingService.getBillingByPatientId(patientId, status));
    }

}
