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
public class AppointmentSystemicExaminationResponse {

    private UUID id;

    private UUID bookingId;

    private String cardiovascular;

    private String respiratory;

    private String nervous;

    private String abdomenGi;

    private String locomotor;

}