package com.ayurveda.appointment.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.client.DocumentUploadClient;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.CreateMedicalAssessmentRequest;
import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;
import com.ayurveda.appointment.dto.response.MedicalAssessmentResponse;
import com.ayurveda.appointment.enums.DocumentType;
import com.ayurveda.appointment.service.AppointmentAyurvedicAssessmentService;
import com.ayurveda.appointment.service.AppointmentLifestyleInformationService;
import com.ayurveda.appointment.service.AppointmentMedicalHistoryService;
import com.ayurveda.appointment.service.AppointmentPhysicalExaminationService;
import com.ayurveda.appointment.service.AppointmentSystemicExaminationService;
import com.ayurveda.appointment.service.AppointmentTreatmentPlanService;
import com.ayurveda.appointment.service.MedicalAssessmentService;
import com.ayurveda.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicalAssessmentServiceImpl implements MedicalAssessmentService {

    private final AppointmentAyurvedicAssessmentService ayurvedicAssessmentService;
    private final AppointmentPhysicalExaminationService physicalExaminationService;
    private final AppointmentMedicalHistoryService medicalHistoryService;
    private final AppointmentLifestyleInformationService lifestyleInformationService;
    private final AppointmentSystemicExaminationService systemicExaminationService;
    private final AppointmentTreatmentPlanService treatmentPlanService;
    private final DocumentUploadClient documentUploadClient;

    @Override
    public ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request) {
        return saveMedicalAssessment(request, Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
    }

    @Override
    public ApiResponse<MedicalAssessmentResponse> saveMedicalAssessment(
            CreateMedicalAssessmentRequest request,
            List<MultipartFile> pastMedicalReports,
            List<MultipartFile> prescriptions,
            List<MultipartFile> labReports) {

        log.info("Saving complete medical assessment for booking {}", request.getBookingId());

        applyBookingId(request);

        var ayurvedic = ayurvedicAssessmentService
                .saveAyurvedicAssessment(request.getAyurvedicAssessment())
                .getData();

        var physical = physicalExaminationService
                .savePhysicalExamination(request.getPhysicalExamination())
                .getData();

        var medicalHistory = medicalHistoryService
                .saveMedicalHistory(request.getMedicalHistory())
                .getData();

        var lifestyle = lifestyleInformationService
                .saveLifestyleInformation(request.getLifestyleInformation())
                .getData();

        var systemic = systemicExaminationService
                .saveSystemicExamination(request.getSystemicExamination())
                .getData();

        var treatmentPlan = treatmentPlanService
                .saveTreatmentPlan(request.getTreatmentPlan())
                .getData();

        List<AppointmentDocumentResponse> documents = new ArrayList<>();
        uploadDocuments(request.getBookingId(), pastMedicalReports,
                DocumentType.PAST_MEDICAL_REPORT, documents);
        uploadDocuments(request.getBookingId(), prescriptions,
                DocumentType.PRESCRIPTION, documents);
        uploadDocuments(request.getBookingId(), labReports,
                DocumentType.LAB_REPORT, documents);

        MedicalAssessmentResponse response = MedicalAssessmentResponse.builder()
                .bookingId(request.getBookingId())
                .ayurvedicAssessment(ayurvedic)
                .physicalExamination(physical)
                .medicalHistory(medicalHistory)
                .lifestyleInformation(lifestyle)
                .systemicExamination(systemic)
                .treatmentPlan(treatmentPlan)
                .documents(documents)
                .build();

        return ApiResponse.success(Constants.MEDICAL_ASSESSMENT_SAVED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<MedicalAssessmentResponse> getMedicalAssessmentByBookingId(UUID bookingId) {

        MedicalAssessmentResponse response = MedicalAssessmentResponse.builder()
                .bookingId(bookingId)
                .ayurvedicAssessment(
                        ayurvedicAssessmentService.getAyurvedicAssessmentByBookingId(bookingId).getData())
                .physicalExamination(
                        physicalExaminationService.getPhysicalExaminationByBookingId(bookingId).getData())
                .medicalHistory(
                        medicalHistoryService.getMedicalHistoryByBookingId(bookingId).getData())
                .lifestyleInformation(
                        lifestyleInformationService.getLifestyleInformationByBookingId(bookingId).getData())
                .systemicExamination(
                        systemicExaminationService.getSystemicExaminationByBookingId(bookingId).getData())
                .treatmentPlan(
                        treatmentPlanService.getTreatmentPlanByBookingId(bookingId).getData())
                .documents(Collections.emptyList())
                .build();

        return ApiResponse.success(Constants.MEDICAL_ASSESSMENT_FETCHED, response);
    }

    private void applyBookingId(CreateMedicalAssessmentRequest request) {
        UUID bookingId = request.getBookingId();
        request.getAyurvedicAssessment().setBookingId(bookingId);
        request.getPhysicalExamination().setBookingId(bookingId);
        request.getMedicalHistory().setBookingId(bookingId);
        request.getLifestyleInformation().setBookingId(bookingId);
        request.getSystemicExamination().setBookingId(bookingId);
        request.getTreatmentPlan().setBookingId(bookingId);
    }

    private void uploadDocuments(
            UUID bookingId,
            List<MultipartFile> files,
            DocumentType documentType,
            List<AppointmentDocumentResponse> responses) {

        if (files == null || files.isEmpty()) {
            return;
        }

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            responses.add(documentUploadClient.uploadDocument(bookingId, documentType, file));
        }
    }

}
