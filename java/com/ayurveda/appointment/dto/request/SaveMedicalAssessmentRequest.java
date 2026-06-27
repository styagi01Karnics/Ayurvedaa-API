package com.ayurveda.appointment.dto.request;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveMedicalAssessmentRequest {

    private UUID bookingId;

    private AyurvedicAssessmentRequest ayurvedicAssessment;

    private PhysicalExaminationRequest physicalExamination;

    private MedicalHistoryRequest medicalHistory;

    private LifestyleInformationRequest lifestyleInformation;

    private SystemicExaminationRequest systemicExamination;

    private TreatmentPlanRequest treatmentPlan;

    private List<DocumentRequest> documents;

}