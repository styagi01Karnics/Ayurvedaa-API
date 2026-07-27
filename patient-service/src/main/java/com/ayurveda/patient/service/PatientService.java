package com.ayurveda.patient.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.request.CreatePatientRequest;
import com.ayurveda.patient.dto.response.PatientResponse;

import java.util.List;
import java.util.UUID;

public interface PatientService {

    ApiResponse<PatientResponse> createPatient(CreatePatientRequest request);

    ApiResponse<PatientResponse> getPatientById(UUID patientId);

    ApiResponse<List<PatientResponse>> getAllPatients();

    ApiResponse<Long> getTotalPatientCount();

    ApiResponse<Void> deletePatient(UUID patientId);
}
