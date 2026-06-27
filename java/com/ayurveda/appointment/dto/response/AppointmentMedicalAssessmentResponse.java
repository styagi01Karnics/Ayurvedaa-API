package com.ayurveda.appointment.dto.response;

import java.util.List;
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
public class AppointmentMedicalAssessmentResponse {

    private UUID bookingId;

    private AyurvedicAssessmentResponse ayurvedicAssessment;

    private PhysicalExaminationResponse physicalExamination;

    private MedicalHistoryResponse medicalHistory;

    private LifestyleInformationResponse lifestyleInformation;

    private SystemicExaminationResponse systemicExamination;

    private TreatmentPlanResponse treatmentPlan;

    private List<DocumentResponse> documents;

}
