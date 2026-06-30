package com.ayurveda.appointment.dto.response;

import java.util.UUID;

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
public class DoctorSummaryResponse {

    private UUID id;
    private String doctorName;
    private String doctorCode;
    private String specialization;
    private String mobileNumber;
    private String email;
    private String qualification;
    private String department;
    private String consultationRoom;

}
