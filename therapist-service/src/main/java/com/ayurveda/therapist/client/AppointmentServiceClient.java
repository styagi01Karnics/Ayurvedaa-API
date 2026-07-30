package com.ayurveda.therapist.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.therapist.dto.client.TherapyMasterClientResponse;

@FeignClient(name = "appointment-service", url = "${services.appointment.url}")
public interface AppointmentServiceClient {

    @GetMapping("/api/v1/therapies/{therapyId}")
    ApiResponse<TherapyMasterClientResponse> getTherapyById(@PathVariable("therapyId") UUID therapyId);

    @GetMapping("/api/v1/therapies")
    ApiResponse<List<TherapyMasterClientResponse>> getAllTherapies();

}
