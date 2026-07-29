package com.ayurveda.doctor.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.doctor.enums.DoctorStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponse {

    private UUID id;
    private String name;
    private String specialization;
    private DoctorStatus status;
    private BigDecimal consultationFees;
    private BigDecimal followUpFees;
    private String availability;

}
