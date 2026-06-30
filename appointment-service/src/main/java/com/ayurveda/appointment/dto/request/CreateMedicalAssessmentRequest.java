package com.ayurveda.appointment.dto.request;

import jakarta.validation.Valid;
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
public class CreateMedicalAssessmentRequest {

    @NotNull(message = "Booking Id is required")
    private java.util.UUID bookingId;

    @NotNull(message = "Ayurvedic assessment is required")
    @Valid
    private CreateAppointmentAyurvedicAssessmentRequest ayurvedicAssessment;

    @NotNull(message = "Physical examination is required")
    @Valid
    private CreateAppointmentPhysicalExaminationRequest physicalExamination;

    @NotNull(message = "Medical history is required")
    @Valid
    private CreateAppointmentMedicalHistoryRequest medicalHistory;

    @NotNull(message = "Lifestyle information is required")
    @Valid
    private CreateAppointmentLifestyleInformationRequest lifestyleInformation;

    @NotNull(message = "Systemic examination is required")
    @Valid
    private CreateAppointmentSystemicExaminationRequest systemicExamination;

    @NotNull(message = "Treatment plan is required")
    @Valid
    private CreateAppointmentTreatmentPlanRequest treatmentPlan;

}
