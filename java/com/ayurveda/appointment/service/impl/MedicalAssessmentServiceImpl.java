package com.ayurveda.appointment.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.request.UploadAppointmentDocumentRequest;
import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.appointment.enums.DocumentType;
import com.ayurveda.appointment.service.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalAssessmentServiceImpl
        implements MedicalAssessmentService {

    private final AppointmentAyurvedicAssessmentService ayurvedicAssessmentService;
    private final AppointmentPhysicalExaminationService physicalExaminationService;
    private final AppointmentMedicalHistoryService medicalHistoryService;
    private final AppointmentLifestyleInformationService lifestyleInformationService;
    private final AppointmentSystemicExaminationService systemicExaminationService;
    private final AppointmentTreatmentPlanService treatmentPlanService;
    private final AppointmentDocumentService appointmentDocumentService;

    @Override
    public ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request) {

        log.info("Saving complete medical assessment for booking {}",
                request.getBookingId());

        request.getAyurvedicAssessment().setBookingId(request.getBookingId());
        request.getPhysicalExamination().setBookingId(request.getBookingId());
        request.getMedicalHistory().setBookingId(request.getBookingId());
        request.getLifestyleInformation().setBookingId(request.getBookingId());
        request.getSystemicExamination().setBookingId(request.getBookingId());
        request.getTreatmentPlan().setBookingId(request.getBookingId());

        var ayurvedic =
                ayurvedicAssessmentService
                        .saveAyurvedicAssessment(request.getAyurvedicAssessment())
                        .getData();

        var physical =
                physicalExaminationService
                        .savePhysicalExamination(request.getPhysicalExamination())
                        .getData();

        var medicalHistory =
                medicalHistoryService
                        .saveMedicalHistory(request.getMedicalHistory())
                        .getData();

        var lifestyle =
                lifestyleInformationService
                        .saveLifestyleInformation(request.getLifestyleInformation())
                        .getData();

        var systemic =
                systemicExaminationService
                        .saveSystemicExamination(request.getSystemicExamination())
                        .getData();

        var treatmentPlan =
                treatmentPlanService
                        .saveTreatmentPlan(request.getTreatmentPlan())
                        .getData();

        List<AppointmentDocumentResponse> documents = new ArrayList<>();

        uploadDocuments(request.getBookingId(),
                request.getPastMedicalReports(),
                DocumentType.PAST_MEDICAL_REPORT,
                documents);

        uploadDocuments(request.getBookingId(),
                request.getPrescriptions(),
                DocumentType.PRESCRIPTION,
                documents);

        uploadDocuments(request.getBookingId(),
                request.getLabReports(),
                DocumentType.LAB_REPORT,
                documents);

        MedicalAssessmentResponse response =
                MedicalAssessmentResponse.builder()
                        .bookingId(request.getBookingId())
                        .ayurvedicAssessment(ayurvedic)
                        .physicalExamination(physical)
                        .medicalHistory(medicalHistory)
                        .lifestyleInformation(lifestyle)
                        .systemicExamination(systemic)
                        .treatmentPlan(treatmentPlan)
                        .documents(documents)
                        .build();

        return ApiResponse.success(
                Constants.MEDICAL_ASSESSMENT_SAVED,
                response);
    }

    private void uploadDocuments(
            java.util.UUID bookingId,
            List<MultipartFile> files,
            DocumentType documentType,
            List<AppointmentDocumentResponse> responses) {

        if (files == null || files.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {

            UploadAppointmentDocumentRequest request =
                    UploadAppointmentDocumentRequest.builder()
                            .bookingId(bookingId)
                            .documentType(documentType)
                            .file(file)
                            .build();

            responses.add(
                    appointmentDocumentService
                            .uploadDocument(request)
                            .getData());
        }
    }

}