package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.service.AppointmentBookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Validated
public class AppointmentBookingController {

    private final AppointmentBookingService appointmentBookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> createAppointment(
            @Valid @RequestBody CreateAppointmentBookingRequest request) {

        ApiResponse<AppointmentBookingResponse> response =
                appointmentBookingService.createAppointment(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> getAppointmentById(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentBookingResponse> response =
                appointmentBookingService.getAppointmentById(bookingId);

        return ResponseEntity.ok(response);
    }
}