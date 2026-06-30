package com.ayurveda.doctor.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DoctorResponse {

    private UUID id;
    private String doctorName;
    private String doctorCode;
    private String specialization;
    private String mobileNumber;
    private String email;
    private String qualification;
    private String department;
    private String consultationRoom;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
