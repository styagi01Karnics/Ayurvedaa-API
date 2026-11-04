package com.ayurveda.fileupload.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.fileupload.dto.response.PatientSummaryClientResponse;

@FeignClient(name = "patient-service", url = "${services.patient.url}")
public interface PatientServiceClient {

    @GetMapping("/api/v1/patients/get-patient/{patientId}")
    ApiResponse<PatientSummaryClientResponse> getPatientById(@PathVariable("patientId") UUID patientId);

}
