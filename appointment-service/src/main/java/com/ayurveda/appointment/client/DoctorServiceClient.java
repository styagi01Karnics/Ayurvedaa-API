package com.ayurveda.appointment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.appointment.dto.response.DoctorSummaryResponse;
import com.ayurveda.common.ApiResponse;

@FeignClient(name = "doctor-service", url = "${services.doctor.url}")
public interface DoctorServiceClient {

    @GetMapping("/api/v1/doctors/{doctorId}")
    ApiResponse<DoctorSummaryResponse> getDoctorById(@PathVariable("doctorId") UUID doctorId);

}
