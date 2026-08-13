package com.ayurveda.billing.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.billing.constant.BillingMessages;
import com.ayurveda.billing.dto.response.InvoiceListResponse;
import com.ayurveda.billing.dto.response.PatientBillingResponse;
import com.ayurveda.billing.dto.response.PatientPackageResponse;
import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.service.InvoiceService;
import com.ayurveda.billing.service.PatientBillingService;
import com.ayurveda.billing.service.PatientPackageService;
import com.ayurveda.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientBillingServiceImpl implements PatientBillingService {

    private final InvoiceService invoiceService;
    private final PatientPackageService patientPackageService;

    @Override
    public ApiResponse<PatientBillingResponse> getBillingByPatientId(
            UUID patientId, InvoiceStatus status) {

        log.info("Fetching billing data for patientId={}, status={}", patientId, status);

        List<InvoiceListResponse> invoices = invoiceService
                .getInvoicesByPatientId(patientId, status)
                .getData();
        if (invoices == null) {
            invoices = List.of();
        }

        List<PatientPackageResponse> packages = patientPackageService
                .getPackagesByPatientId(patientId)
                .getData();
        if (packages == null) {
            packages = List.of();
        }

        PatientBillingResponse.BillingSummary summary = buildSummary(invoices, packages);

        PatientBillingResponse response = PatientBillingResponse.builder()
                .patientId(patientId)
                .summary(summary)
                .invoices(invoices)
                .packages(packages)
                .build();

        log.info(
                "Fetched billing for patientId={}: {} invoices, {} packages",
                patientId,
                invoices.size(),
                packages.size());

        return ApiResponse.success(BillingMessages.PATIENT_BILLING_FETCHED_SUCCESSFULLY, response);
    }

    private PatientBillingResponse.BillingSummary buildSummary(
            List<InvoiceListResponse> invoices,
            List<PatientPackageResponse> packages) {

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal leftAmount = BigDecimal.ZERO;
        int unpaidCount = 0;
        int ongoingCount = 0;
        int completedCount = 0;

        for (InvoiceListResponse invoice : invoices) {
            totalAmount = totalAmount.add(nullToZero(invoice.getTotalAmount()));
            paidAmount = paidAmount.add(nullToZero(invoice.getPaidAmount()));
            leftAmount = leftAmount.add(nullToZero(invoice.getLeftAmount()));

            if (invoice.getStatus() == InvoiceStatus.UNPAID) {
                unpaidCount++;
            } else if (invoice.getStatus() == InvoiceStatus.ONGOING) {
                ongoingCount++;
            } else if (invoice.getStatus() == InvoiceStatus.COMPLETED) {
                completedCount++;
            }
        }

        return PatientBillingResponse.BillingSummary.builder()
                .invoiceCount(invoices.size())
                .unpaidCount(unpaidCount)
                .ongoingCount(ongoingCount)
                .completedCount(completedCount)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .leftAmount(leftAmount)
                .packageCount(packages.size())
                .build();
    }

    private BigDecimal nullToZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

}
