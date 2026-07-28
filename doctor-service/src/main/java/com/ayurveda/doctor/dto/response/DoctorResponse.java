package com.ayurveda.doctor.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.doctor.enums.DoctorStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DoctorResponse {

    private Integer serialNo;
    private UUID id;
    private String name;
    private String doctorName;
    private String doctorCode;
    private String specialization;
    private DoctorStatus status;
    private BigDecimal consultationFees;
    private BigDecimal followUpFees;
    private String availability;
    private String mobileNumber;
    private String email;
    private String qualification;
    private String department;
    private String consultationRoom;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
