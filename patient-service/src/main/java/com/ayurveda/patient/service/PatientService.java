package com.ayurveda.patient.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.PatientResponse;

import java.util.List;
import java.util.UUID;

public interface PatientService {

    /** Creates a new patient record. */
    ApiResponse<PatientResponse> createPatient(CreatePatientRequest request);

    /** Returns a patient by ID. */
    ApiResponse<PatientResponse> getPatientById(UUID patientId);

    /** Lists all patients for the current tenant. */
    ApiResponse<List<PatientResponse>> getAllPatients();

    /** Returns the total patient count for the current tenant. */
    ApiResponse<Long> getTotalPatientCount();

    /** Soft-deletes a patient by ID. */
    ApiResponse<Void> deletePatient(UUID patientId);
}
