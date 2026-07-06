package com.ayurveda.appointment.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.appointment.dto.response.TherapistSummaryResponse;
import com.ayurveda.common.ApiResponse;

@FeignClient(name = "therapist-service", url = "${services.therapist.url}")
public interface TherapistServiceClient {

    @GetMapping("/api/v1/therapists/{therapistId}")
    ApiResponse<TherapistSummaryResponse> getTherapistById(@PathVariable("therapistId") UUID therapistId);

}
