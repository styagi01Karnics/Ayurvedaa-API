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

    ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request);

    ApiResponse<AppointmentBookingResponse> getAppointmentById(
            UUID bookingId);

    ApiResponse<List<PatientAppointmentListItemResponse>> getPatientList(
            PatientListTab statusTab,
            String search,
            BookingStatus bookingStatus,
            ConsultationType consultationType,
            UUID doshaId,
            UUID doctorId);

    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByPatientId(UUID patientId);

    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByBookingStatus(
            BookingStatus bookingStatus);

    ApiResponse<List<AppointmentBookingResponse>> getAppointmentsByDate(
            LocalDate registrationDate);

    ApiResponse<AppointmentStatsResponse> getAppointmentStats();

    ApiResponse<List<AppointmentBookingResponse>> getCancelledAppointments();

    ApiResponse<List<AppointmentBookingResponse>> getTodayAppointmentsByConsultationType(
            ConsultationType consultationType);

    ApiResponse<DoctorTodayScheduleResponse> getDoctorTodaySchedule(UUID doctorId);

    /** Dashboard page – Today's Schedule card. */
    ApiResponse<DashboardTodaysScheduleResponse> getDashboardTodaysSchedule(UUID doctorId);

    ApiResponse<AppointmentBookingResponse> rescheduleAppointment(
            UUID bookingId, RescheduleAppointmentBookingRequest request);

    ApiResponse<AppointmentBookingResponse> cancelAppointment(UUID bookingId);

    ApiResponse<Void> deleteAppointment(UUID bookingId);

    ApiResponse<AppointmentBookingResponse> markInConsultation(UUID bookingId);

    ApiResponse<AppointmentBookingResponse> markCompleted(UUID bookingId);

}
