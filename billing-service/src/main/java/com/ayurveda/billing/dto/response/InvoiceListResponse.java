package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.BillSection;
import com.ayurveda.billing.enums.InvoiceStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceListResponse {

    private UUID id;
    private String invoiceId;
    private UUID patientId;
    private String patientDisplayId;
    private String formattedPatientId;
    private String patientCode;
    private String patientName;
    private LocalDate invoiceDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal leftAmount;
    private InvoiceStatus status;
    private List<BillSection> billSections;

}
