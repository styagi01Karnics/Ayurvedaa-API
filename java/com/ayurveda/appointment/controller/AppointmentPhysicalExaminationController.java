package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.appointment.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;
import com.ayurveda.appointment.service.AppointmentPhysicalExaminationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/physical-examinations")
@RequiredArgsConstructor
@Validated
public class AppointmentPhysicalExaminationController {

    private final AppointmentPhysicalExaminationService
            appointmentPhysicalExaminationService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentPhysicalExaminationResponse>>
            savePhysicalExamination(
            @Valid @RequestBody CreateAppointmentPhysicalExaminationRequest request) {

        ApiResponse<AppointmentPhysicalExaminationResponse> response =
                appointmentPhysicalExaminationService
                        .savePhysicalExamination(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<AppointmentPhysicalExaminationResponse>>
            getPhysicalExaminationByBookingId(
            @PathVariable UUID bookingId) {

        ApiResponse<AppointmentPhysicalExaminationResponse> response =
                appointmentPhysicalExaminationService
                        .getPhysicalExaminationByBookingId(bookingId);

        return ResponseEntity.ok(response);
    }

}