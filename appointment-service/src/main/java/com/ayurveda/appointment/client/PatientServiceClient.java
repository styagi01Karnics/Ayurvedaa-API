package com.ayurveda.appointment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ayurveda.appointment.dto.request.CreatePatientClientRequest;
import com.ayurveda.appointment.dto.response.PatientSummaryResponse;
import com.ayurveda.common.ApiResponse;

@FeignClient(name = "patient-service", url = "${services.patient.url}")
public interface PatientServiceClient {

    @PostMapping("/api/v1/patients")
    ApiResponse<PatientSummaryResponse> createPatient(@RequestBody CreatePatientClientRequest request);

    @GetMapping("/api/v1/patients/{patientId}")
    ApiResponse<PatientSummaryResponse> getPatientById(@PathVariable("patientId") UUID patientId);

}
