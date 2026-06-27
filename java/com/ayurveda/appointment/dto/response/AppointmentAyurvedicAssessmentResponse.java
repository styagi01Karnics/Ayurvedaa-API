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
public class AppointmentAyurvedicAssessmentResponse {

    private UUID id;

    private UUID bookingId;

    private String doshaType;

    private String bodyConstitution;

    private String currentImbalances;

}