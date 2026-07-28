package com.ayurveda.billing.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ayurveda.billing.dto.client.AppointmentTherapyClientResponse;
import com.ayurveda.common.ApiResponse;

@FeignClient(name = "appointment-service", url = "${services.appointment.url}")
public interface AppointmentServiceClient {

    @GetMapping("/api/v1/appointment-therapies/{patientId}")
    ApiResponse<List<AppointmentTherapyClientResponse>> getTherapiesByPatientId(
            @PathVariable("patientId") UUID patientId);

}
