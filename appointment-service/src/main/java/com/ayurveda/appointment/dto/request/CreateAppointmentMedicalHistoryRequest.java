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
public class CreateAppointmentMedicalHistoryRequest {

    /** Required for standalone POST; filled from parent for medical-assessment aggregate. */
    private UUID patientId;

    private String pastMedicalConditions;

    private String pastSurgeries;

    private String currentMedications;

    private String allergies;

    private String familyHistory;

}