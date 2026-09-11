package com.ayurveda.patient.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.DashboardNewPatientsMonthlyResponse;
import com.ayurveda.patient.dto.response.PatientCountResponse;
import com.ayurveda.patient.dto.response.PatientResponse;

import java.util.UUID;

public interface PatientService {

    /** Creates a new patient record. */
    ApiResponse<PatientResponse> createPatient(CreatePatientRequest request);

    /** Returns a patient by ID. */
    ApiResponse<PatientResponse> getPatientById(UUID patientId);

    /** Lists patients for the current tenant (paginated). */
    ApiResponse<PagedResponse<PatientResponse>> getAllPatients(int page, int size);

    /** Returns total, active, and inactive patient counts for the current tenant. */
    ApiResponse<PatientCountResponse> getTotalPatientCount();

    /** Dashboard chart – new patients registered per month in the given year. */
    ApiResponse<DashboardNewPatientsMonthlyResponse> getDashboardNewPatientsByMonth(Integer year);

    /** Soft-deletes a patient by ID. */
    ApiResponse<Void> deletePatient(UUID patientId);
}
