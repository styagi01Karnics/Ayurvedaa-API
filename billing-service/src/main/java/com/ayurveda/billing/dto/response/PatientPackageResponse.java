package com.ayurveda.billing.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.billing.enums.PackageStatus;

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
public class PatientPackageResponse {

    private UUID id;
    private UUID patientId;
    private UUID packageMasterId;
    private String packageName;
    private BigDecimal packagePrice;
    private LocalDate validity;
    private PackageStatus status;
    private BigDecimal discountApplied;

}
