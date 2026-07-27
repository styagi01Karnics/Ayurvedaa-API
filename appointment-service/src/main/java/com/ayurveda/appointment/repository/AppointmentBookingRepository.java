package com.ayurveda.appointment.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.ConsultationType;

@Repository
public interface AppointmentBookingRepository
        extends JpaRepository<AppointmentBooking, UUID> {

    boolean existsByPatientId(UUID patientId);

    List<AppointmentBooking> findByPatientId(UUID patientId);

    List<AppointmentBooking> findByBookingStatusAndDeletedFalse(BookingStatus bookingStatus);

    List<AppointmentBooking> findByRegistrationDateAndDeletedFalse(LocalDate registrationDate);

    long countByRegistrationDateBetweenAndDeletedFalseAndBookingStatusNot(
            LocalDate startDate, LocalDate endDate, BookingStatus bookingStatus);

    long countByRegistrationDateBetweenAndDeletedFalseAndBookingStatus(
            LocalDate startDate, LocalDate endDate, BookingStatus bookingStatus);

    long countByRegistrationDateAndDeletedFalseAndBookingStatusNot(
            LocalDate registrationDate, BookingStatus bookingStatus);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND a.id IN (
                  SELECT c.bookingId FROM AppointmentConsultationType c
                  WHERE c.consultationType = :consultationType
              )
            """)
    List<AppointmentBooking> findByDateAndConsultationType(
            @Param("date") LocalDate date,
            @Param("consultationType") ConsultationType consultationType,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.assignedDoctorId = :doctorId
              AND a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
            ORDER BY a.createdAt ASC
            """)
    List<AppointmentBooking> findByDoctorAndDateExcludingCancelled(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            ORDER BY a.createdAt ASC
            """)
    List<AppointmentBooking> findTodaySchedule(
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled,
            @Param("doctorId") UUID doctorId);

}
