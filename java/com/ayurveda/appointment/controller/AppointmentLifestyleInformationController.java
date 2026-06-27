package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentLifestyleInformationRequest;
import com.ayurveda.appointment.dto.response.AppointmentLifestyleInformationResponse;
import com.ayurveda.appointment.service.AppointmentLifestyleInformationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/lifestyle-information")
@RequiredArgsConstructor
@Validated
public class AppointmentLifestyleInformationController {

    private final AppointmentLifestyleInformationService
            appointmentLifestyleInformationService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentLifestyleInformationResponse>>
            saveLifestyleInformation(
            @Valid @RequestBody CreateAppointmentLifestyleInformationRequest request) {

        ApiResponse<AppointmentLifestyleInformationResponse> response =
                appointmentLifestyleInformationService
                        .saveLifestyleInformation(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentLifestyleInformationResponse>>
            getLifestyleInformationByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentLifestyleInformationResponse> response =
                appointmentLifestyleInformationService
                        .getLifestyleInformationByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}