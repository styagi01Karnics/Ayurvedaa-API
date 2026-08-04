package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentRequest;
import com.ayurveda.appointment.dto.request.UpdateTreatmentStatusRequest;
import com.ayurveda.appointment.dto.response.TreatmentResponse;
import com.ayurveda.common.ApiResponse;

public interface TreatmentService {

    /** Creates a treatment plan for a patient. */
    ApiResponse<TreatmentResponse> createTreatment(CreateTreatmentRequest request);

    /** Returns all non-deleted treatments. */
    ApiResponse<List<TreatmentResponse>> getAllTreatments();

    /** Returns non-deleted treatments for a patient. */
    ApiResponse<List<TreatmentResponse>> getTreatmentsByPatientId(UUID patientId);

    /** Updates treatment plan details (not status). */
    ApiResponse<TreatmentResponse> updateTreatment(UUID treatmentId, UpdateTreatmentRequest request);

    /** Changes treatment status by treatment id (SCHEDULED, ONGOING, COMPLETED). */
    ApiResponse<TreatmentResponse> updateTreatmentStatus(
            UUID treatmentId, UpdateTreatmentStatusRequest request);

}
