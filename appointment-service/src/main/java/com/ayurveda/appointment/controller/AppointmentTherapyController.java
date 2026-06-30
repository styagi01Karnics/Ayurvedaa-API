package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentTherapyRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.service.AppointmentTherapyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Appointment Therapy", description = "Appointment therapy assignment APIs")
@RestController
@RequestMapping("/api/v1/appointment-therapies")
@RequiredArgsConstructor
@Validated
public class AppointmentTherapyController {

    private final AppointmentTherapyService appointmentTherapyService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentTherapyResponse>>
            createAppointmentTherapy(
            @Valid @RequestBody CreateAppointmentTherapyRequest request) {

        ApiResponse<AppointmentTherapyResponse> response =
                appointmentTherapyService
                        .createAppointmentTherapy(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentTherapyResponse>>
            getAppointmentTherapyByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentTherapyResponse> response =
                appointmentTherapyService
                        .getAppointmentTherapyByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}