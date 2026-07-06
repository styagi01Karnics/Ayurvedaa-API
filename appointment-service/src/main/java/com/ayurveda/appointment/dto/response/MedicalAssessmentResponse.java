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
public class MedicalAssessmentResponse {

    private UUID patientId;

    private AppointmentAyurvedicAssessmentResponse ayurvedicAssessment;

    private AppointmentPhysicalExaminationResponse physicalExamination;

    private AppointmentMedicalHistoryResponse medicalHistory;

    private AppointmentLifestyleInformationResponse lifestyleInformation;

    private AppointmentSystemicExaminationResponse systemicExamination;

    private AppointmentTreatmentPlanResponse treatmentPlan;

    private java.util.List<AppointmentDocumentResponse> documents;

}