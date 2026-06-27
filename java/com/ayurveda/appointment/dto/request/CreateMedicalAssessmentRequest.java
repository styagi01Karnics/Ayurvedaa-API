package com.ayurveda.appointment.dto.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicalAssessmentRequest {

    @NotNull(message = "Booking Id is required")
    private java.util.UUID bookingId;

    @Valid
    private CreateAppointmentAyurvedicAssessmentRequest ayurvedicAssessment;

    @Valid
    private CreateAppointmentPhysicalExaminationRequest physicalExamination;

    @Valid
    private CreateAppointmentMedicalHistoryRequest medicalHistory;

    @Valid
    private CreateAppointmentLifestyleInformationRequest lifestyleInformation;

    @Valid
    private CreateAppointmentSystemicExaminationRequest systemicExamination;

    @Valid
    private CreateAppointmentTreatmentPlanRequest treatmentPlan;

    private List<MultipartFile> pastMedicalReports;

    private List<MultipartFile> prescriptions;

    private List<MultipartFile> labReports;

}