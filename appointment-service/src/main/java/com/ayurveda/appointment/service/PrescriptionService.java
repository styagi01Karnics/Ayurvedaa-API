package com.ayurveda.appointment.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest;
import com.ayurveda.appointment.dto.request.UpdatePrescriptionRequest;
import com.ayurveda.appointment.dto.response.PrescriptionResponse;
import com.ayurveda.common.ApiResponse;

public interface PrescriptionService {

    /** Creates a prescription (medicines, therapy suggestions, optional next follow-up). */
    ApiResponse<PrescriptionResponse> createPrescription(CreatePrescriptionRequest request);

    /** Updates an existing prescription by id (replaces medicines and therapy suggestions). */
    ApiResponse<PrescriptionResponse> updatePrescription(
            UUID prescriptionId, UpdatePrescriptionRequest request);

    /** Returns one non-deleted prescription by id. */
    ApiResponse<PrescriptionResponse> getPrescriptionById(UUID prescriptionId);

    /** Returns all non-deleted prescriptions for a patient. */
    ApiResponse<List<PrescriptionResponse>> getPrescriptionsByPatientId(UUID patientId);

}
