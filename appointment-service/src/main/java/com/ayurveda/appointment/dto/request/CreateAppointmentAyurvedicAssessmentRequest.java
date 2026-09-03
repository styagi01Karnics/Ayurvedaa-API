package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
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
public class CreateAppointmentAyurvedicAssessmentRequest {

    /** Required for standalone POST; filled from parent for medical-assessment aggregate. */
    private UUID patientId;

    @NotNull(message = "Dosha Id is required")
    private UUID doshaId;

    private String bodyConstitution;

    private String currentImbalances;

}