package com.ayurveda.appointment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.service.AppointmentBookingService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Appointment Booking", description = "Create and manage appointment bookings")
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
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getAppointmentsByPatientId(
            @PathVariable UUID patientId) {

        ApiResponse<List<AppointmentBookingResponse>> response =
                appointmentBookingService.getAppointmentsByPatientId(patientId);

        return ResponseEntity.ok(response);
    }
}