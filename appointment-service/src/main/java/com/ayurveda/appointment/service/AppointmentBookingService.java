package com.ayurveda.appointment.service;

import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreateAppointmentBookingRequest;
import com.ayurveda.appointment.dto.request.RescheduleAppointmentBookingRequest;
import com.ayurveda.appointment.dto.response.AppointmentBookingResponse;
import com.ayurveda.appointment.dto.response.AppointmentStatsResponse;
import com.ayurveda.appointment.dto.response.DashboardTodaysScheduleResponse;
import com.ayurveda.appointment.dto.response.DoctorTodayScheduleResponse;
import com.ayurveda.appointment.dto.response.PatientAppointmentListItemResponse;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.PatientListTab;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

public interface AppointmentBookingService {

    ApiResponse<AppointmentBookingResponse> createAppointment(
            CreateAppointmentBookingRequest request);

    ApiResponse<AppointmentBookingResponse> getAppointmentById(UUID bookingId);

    ApiResponse<PagedResponse<PatientAppointmentListItemResponse>> getPatientList(
            PatientListTab statusTab,
            String search,
            BookingStatus bookingStatus,
            UUID consultationTypeId,
            UUID doshaId,
            UUID doctorId,
            int page,
            int size);

    ApiResponse<PagedResponse<AppointmentBookingResponse>> getAppointmentsByPatientId(
            UUID patientId, int page, int size);

    ApiResponse<PagedResponse<AppointmentBookingResponse>> getAppointmentsByBookingStatus(
            BookingStatus bookingStatus, int page, int size);

    ApiResponse<PagedResponse<AppointmentBookingResponse>> getAppointmentsByDate(
            LocalDate registrationDate, int page, int size);

    ApiResponse<AppointmentStatsResponse> getAppointmentStats();

    ApiResponse<PagedResponse<AppointmentBookingResponse>> getCancelledAppointments(int page, int size);

    ApiResponse<PagedResponse<AppointmentBookingResponse>> getTodayAppointmentsByConsultationType(
            UUID consultationTypeId, int page, int size);

    ApiResponse<DoctorTodayScheduleResponse> getDoctorTodaySchedule(UUID doctorId, int page, int size);

    ApiResponse<DoctorTodayScheduleResponse> getTodayAppointments(int page, int size);

    ApiResponse<DashboardTodaysScheduleResponse> getDashboardTodaysSchedule(UUID doctorId);

    ApiResponse<AppointmentBookingResponse> rescheduleAppointment(
            UUID bookingId, RescheduleAppointmentBookingRequest request);

    ApiResponse<AppointmentBookingResponse> cancelAppointment(UUID bookingId);

    ApiResponse<Void> deleteAppointment(UUID bookingId);

    ApiResponse<AppointmentBookingResponse> markInConsultation(UUID bookingId);

    ApiResponse<AppointmentBookingResponse> markCompleted(UUID bookingId);

}
