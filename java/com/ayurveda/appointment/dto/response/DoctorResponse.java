package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

}