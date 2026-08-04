package com.ayurveda.appointment.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.request.RescheduleAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.AppointmentStatsResponse;
import com.ayurveda.appointment.dto.response.DashboardTodaysScheduleResponse;
import com.ayurveda.appointment.dto.response.DoctorTodayScheduleResponse;
import com.ayurveda.appointment.dto.response.PatientAppointmentListItemResponse;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;
import com.ayurveda.appointment.enums.PatientListTab;

public interface AppointmentBookingService {

    /** Creates a new appointment booking (and patient when needed). */
    ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request);

    /** Returns a non-deleted appointment by booking id. */
    ApiResponse<AppointmentBookingResponse> getAppointmentById(
            UUID bookingId);

    /** Patient list for appointments UI with optional filters. */
    ApiResponse<List<PatientAppointmentListItemResponse>> getPatientList(
            PatientListTab statusTab,
            String search,
            BookingStatus bookingStatus,
            ConsultationType consultationType,
            UUID doshaId,
            UUID doctorId);

    /** Returns all appointments for a patient. */
    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByPatientId(UUID patientId);

    /**
     * Lists non-deleted appointments. When {@code bookingStatus} is null, returns all.
     * Ordered: today first, then tomorrow, day after, then later; past dates after that.
     */
    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByBookingStatus(
            BookingStatus bookingStatus);

    /** Returns non-deleted appointments for a registration date. */
    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByDate(
            LocalDate registrationDate);

    /** Returns month/today appointment statistics. */
    ApiResponse<AppointmentStatsResponse> getAppointmentStats();

    /** Returns cancelled, non-deleted appointments. */
    ApiResponse<List<AppointmentBookingResponse>> getCancelledAppointments();

    /** Returns today's non-cancelled appointments for a consultation type. */
    ApiResponse<List<AppointmentBookingResponse>> getTodayAppointmentsByConsultationType(
            ConsultationType consultationType);

    /** Today's appointments for one doctor (cancelled excluded). */
    ApiResponse<DoctorTodayScheduleResponse> getDoctorTodaySchedule(UUID doctorId);

    /** All doctors – today's appointments (cancelled excluded). */
    ApiResponse<DoctorTodayScheduleResponse> getTodayAppointments();

    /** Dashboard page – Today's Schedule card. */
    ApiResponse<DashboardTodaysScheduleResponse> getDashboardTodaysSchedule(UUID doctorId);

    /**
     * Reschedules an appointment. Allowed from SCHEDULED, RESCHEDULED, or CANCELLED;
     * result status is RESCHEDULED.
     */
    ApiResponse<AppointmentBookingResponse> rescheduleAppointment(
            UUID bookingId, RescheduleAppointmentBookingRequest request);

    /** Sets booking status to CANCELLED. */
    ApiResponse<AppointmentBookingResponse> cancelAppointment(UUID bookingId);

    /** Soft-deletes an appointment and marks it CANCELLED. */
    ApiResponse<Void> deleteAppointment(UUID bookingId);

    /** Moves SCHEDULED/RESCHEDULED appointment to IN_CONSULTATION. */
    ApiResponse<AppointmentBookingResponse> markInConsultation(UUID bookingId);

    /** Moves IN_CONSULTATION appointment to COMPLETED. */
    ApiResponse<AppointmentBookingResponse> markCompleted(UUID bookingId);

}
