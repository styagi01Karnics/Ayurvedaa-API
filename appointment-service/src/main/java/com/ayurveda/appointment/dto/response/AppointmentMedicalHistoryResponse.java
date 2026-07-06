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
public class AppointmentMedicalHistoryResponse {

    private UUID id;

    private UUID patientId;

    private String pastMedicalConditions;

    private String pastSurgeries;

    private String currentMedications;

    private String allergies;

    private String familyHistory;

}