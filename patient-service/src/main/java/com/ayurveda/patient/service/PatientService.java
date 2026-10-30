package com.ayurveda.patient.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.request.UpdatePatientEmailRequest;
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

    /** Updates the email address of a patient by ID. */
    ApiResponse<PatientResponse> updatePatientEmail(UUID patientId, UpdatePatientEmailRequest request);

    /** Soft-deletes a patient by ID. */
    ApiResponse<Void> deletePatient(UUID patientId);
}
