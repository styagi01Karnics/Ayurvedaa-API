package com.ayurveda.appointment.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.request.RescheduleAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.AppointmentStatsResponse;
import com.ayurveda.appointment.dto.response.DoctorTodayScheduleResponse;
import com.ayurveda.appointment.dto.response.PatientAppointmentListItemResponse;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;
import com.ayurveda.appointment.enums.PatientListTab;
import com.ayurveda.appointment.service.AppointmentBookingService;

import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            summary = "Appointment stats",
            description = "Current month count, completed, ongoing (month - completed), and today count. Cancelled excluded.")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<AppointmentStatsResponse>> getAppointmentStats() {

        return ResponseEntity.ok(appointmentBookingService.getAppointmentStats());
    }

    @Operation(
            summary = "Active / Inactive patients list",
            description = """
                    Returns appointment rows for the patients screen.
                    ACTIVE = SCHEDULED, RESCHEDULED.
                    INACTIVE = COMPLETED, CANCELLED.
                    Optional filters: search (patient id/code/name/mobile), status, visit type, dosha, doctor.
                    """)
    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientAppointmentListItemResponse>>> getPatientList(
            @RequestParam PatientListTab statusTab,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BookingStatus bookingStatus,
            @RequestParam(required = false) ConsultationType consultationType,
            @RequestParam(required = false) UUID doshaId,
            @RequestParam(required = false) UUID doctorId) {

        return ResponseEntity.ok(appointmentBookingService.getPatientList(
                statusTab, search, bookingStatus, consultationType, doshaId, doctorId));
    }

    @Operation(
            summary = "Get cancelled appointments",
            description = "Cancelled appointment details including patient phone number.")
    @GetMapping("/cancelled")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getCancelledAppointments() {

        return ResponseEntity.ok(appointmentBookingService.getCancelledAppointments());
    }

    @Operation(
            summary = "Get today's appointments by consultation type",
            description = "Returns today's non-cancelled appointments for CONSULTATION or THERAPY, with patient details.")
    @GetMapping("/today/{consultationType}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getTodayAppointmentsByConsultationType(
            @PathVariable ConsultationType consultationType) {

        return ResponseEntity.ok(
                appointmentBookingService.getTodayAppointmentsByConsultationType(consultationType));
    }

    @Operation(
            summary = "Get doctor's today's appointments",
            description = """
                    Returns only today's appointments for the doctor (cancelled excluded),
                    ordered ascending by booking time (createdAt).
                    No timeslot — uses booking slotTime (ordered ascending).
                    """)
    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<ApiResponse<DoctorTodayScheduleResponse>> getDoctorTodaySchedule(
            @PathVariable UUID doctorId) {

        return ResponseEntity.ok(appointmentBookingService.getDoctorTodaySchedule(doctorId));
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

    @Operation(summary = "Get appointments by booking status")
    @GetMapping("/status/{bookingStatus}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getAppointmentsByBookingStatus(
            @PathVariable BookingStatus bookingStatus) {

        return ResponseEntity.ok(
                appointmentBookingService.getAppointmentsByBookingStatus(bookingStatus));
    }

    @Operation(summary = "Get appointments by registration date")
    @GetMapping("/date/{registrationDate}")
    public ResponseEntity<ApiResponse<List<AppointmentBookingResponse>>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate registrationDate) {

        return ResponseEntity.ok(
                appointmentBookingService.getAppointmentsByDate(registrationDate));
    }

    @Operation(
            summary = "Reschedule appointment",
            description = "Same fields as create, but uses existing patientId. Sets status to RESCHEDULED.")
    @PutMapping("/{bookingId}/reschedule")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> rescheduleAppointment(
            @PathVariable UUID bookingId,
            @Valid @RequestBody RescheduleAppointmentBookingRequest request) {

        return ResponseEntity.ok(
                appointmentBookingService.rescheduleAppointment(bookingId, request));
    }

    @Operation(summary = "Cancel appointment", description = "Sets booking status to CANCELLED.")
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> cancelAppointment(
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(appointmentBookingService.cancelAppointment(bookingId));
    }

    @Operation(summary = "Delete appointment", description = "Soft deletes appointment and sets status to CANCELLED.")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(appointmentBookingService.deleteAppointment(bookingId));
    }

    @Operation(
            summary = "Start consultation",
            description = "Changes status from SCHEDULED or RESCHEDULED to IN_CONSULTATION.")
    @PutMapping("/{bookingId}/in-consultation")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> markInConsultation(
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(appointmentBookingService.markInConsultation(bookingId));
    }

    @Operation(
            summary = "Complete appointment",
            description = "Changes status from IN_CONSULTATION to COMPLETED.")
    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<ApiResponse<AppointmentBookingResponse>> markCompleted(
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(appointmentBookingService.markCompleted(bookingId));
    }
}
