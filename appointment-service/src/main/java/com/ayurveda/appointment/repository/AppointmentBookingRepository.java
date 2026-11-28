package com.ayurveda.appointment.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentBooking;
import com.ayurveda.appointment.enums.BookingStatus;

@Repository
public interface AppointmentBookingRepository
        extends JpaRepository<AppointmentBooking, UUID> {

    boolean existsByPatientId(UUID patientId);

    List<AppointmentBooking> findByPatientId(UUID patientId);

    List<AppointmentBooking> findByBookingStatusAndDeletedFalse(BookingStatus bookingStatus);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.deleted = false
              AND (:status IS NULL OR a.bookingStatus = :status)
            ORDER BY a.createdAt DESC
            """)
    List<AppointmentBooking> findAllByOptionalStatusAndDeletedFalseOrderByCreatedAtDesc(
            @Param("status") BookingStatus status);

    @Query(value = """
            SELECT a FROM AppointmentBooking a
            WHERE a.deleted = false
              AND (:status IS NULL OR a.bookingStatus = :status)
            """,
            countQuery = """
            SELECT COUNT(a) FROM AppointmentBooking a
            WHERE a.deleted = false
              AND (:status IS NULL OR a.bookingStatus = :status)
            """)
    Page<AppointmentBooking> findTablePageByOptionalStatus(
            @Param("status") BookingStatus status,
            Pageable pageable);

    List<AppointmentBooking> findByRegistrationDateAndDeletedFalse(LocalDate registrationDate);

    @Query("""
            SELECT COUNT(a) FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.registrationDate BETWEEN :startDate AND :endDate
              AND a.bookingStatus <> :excludedStatus
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            """)
    long countStatsBetweenExcluding(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludedStatus") BookingStatus excludedStatus,
            @Param("doctorId") UUID doctorId);

    @Query("""
            SELECT COUNT(a) FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.registrationDate BETWEEN :startDate AND :endDate
              AND a.bookingStatus = :status
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            """)
    long countStatsBetweenWithStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") BookingStatus status,
            @Param("doctorId") UUID doctorId);

    @Query("""
            SELECT COUNT(a) FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.registrationDate = :date
              AND a.bookingStatus <> :excludedStatus
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            """)
    long countStatsOnDateExcluding(
            @Param("date") LocalDate date,
            @Param("excludedStatus") BookingStatus excludedStatus,
            @Param("doctorId") UUID doctorId);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND a.id IN (
                  SELECT c.bookingId FROM AppointmentConsultationType c
                  WHERE c.consultationTypeId = :consultationTypeId
              )
            """)
    List<AppointmentBooking> findByDateAndConsultationType(
            @Param("date") LocalDate date,
            @Param("consultationTypeId") UUID consultationTypeId,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.assignedDoctorId = :doctorId
              AND a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
            ORDER BY a.slotTime ASC NULLS LAST, a.createdAt ASC
            """)
    List<AppointmentBooking> findByDoctorAndDateExcludingCancelled(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.assignedDoctorId = :doctorId
              AND a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
            ORDER BY a.slotTime ASC NULLS LAST, a.createdAt ASC
            """)
    Page<AppointmentBooking> findByDoctorAndDateExcludingCancelled(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled,
            Pageable pageable);

    @Query("""
            SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
            FROM AppointmentBooking a
            WHERE a.assignedDoctorId = :doctorId
              AND a.registrationDate = :date
              AND a.slotTime = :slotTime
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND (:excludeBookingId IS NULL OR a.id <> :excludeBookingId)
            """)
    boolean existsDoctorSlotConflict(
            @Param("doctorId") UUID doctorId,
            @Param("date") LocalDate date,
            @Param("slotTime") LocalTime slotTime,
            @Param("cancelled") BookingStatus cancelled,
            @Param("excludeBookingId") UUID excludeBookingId);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            ORDER BY a.slotTime ASC NULLS LAST, a.createdAt ASC
            """)
    List<AppointmentBooking> findTodaySchedule(
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled,
            @Param("doctorId") UUID doctorId);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.registrationDate = :date
              AND a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
            ORDER BY a.slotTime ASC NULLS LAST, a.createdAt ASC
            """)
    Page<AppointmentBooking> findTodaySchedule(
            @Param("date") LocalDate date,
            @Param("cancelled") BookingStatus cancelled,
            @Param("doctorId") UUID doctorId,
            Pageable pageable);

    @Query("""
            SELECT a FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.bookingStatus IN :statuses
              AND (:doctorId IS NULL OR a.assignedDoctorId = :doctorId)
              AND (
                    :consultationTypeId IS NULL
                    OR a.id IN (
                        SELECT c.bookingId FROM AppointmentConsultationType c
                        WHERE c.consultationTypeId = :consultationTypeId
                    )
              )
              AND (
                    :doshaId IS NULL
                    OR a.patientId IN (
                        SELECT aa.patientId FROM AppointmentAyurvedicAssessment aa
                        WHERE aa.doshaId = :doshaId
                          AND aa.deleted = false
                    )
              )
            ORDER BY a.createdAt DESC
            """)
    List<AppointmentBooking> findPatientList(
            @Param("statuses") Collection<BookingStatus> statuses,
            @Param("doctorId") UUID doctorId,
            @Param("consultationTypeId") UUID consultationTypeId,
            @Param("doshaId") UUID doshaId);

    @Query("""
            SELECT EXTRACT(MONTH FROM a.registrationDate), COUNT(a)
            FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.bookingStatus <> :cancelled
              AND a.registrationDate >= :from
              AND a.registrationDate < :to
              AND a.registrationDate = (
                  SELECT MIN(b.registrationDate)
                  FROM AppointmentBooking b
                  WHERE b.patientId = a.patientId
                    AND b.deleted = false
                    AND b.bookingStatus <> :cancelled
              )
            GROUP BY EXTRACT(MONTH FROM a.registrationDate)
            """)
    List<Object[]> countFirstVisitsByMonth(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT EXTRACT(MONTH FROM a.registrationDate), COUNT(a)
            FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.assignedDoctorId = :doctorId
              AND a.bookingStatus <> :cancelled
              AND a.registrationDate >= :from
              AND a.registrationDate < :to
              AND a.registrationDate = (
                  SELECT MIN(b.registrationDate)
                  FROM AppointmentBooking b
                  WHERE b.patientId = a.patientId
                    AND b.assignedDoctorId = :doctorId
                    AND b.deleted = false
                    AND b.bookingStatus <> :cancelled
              )
            GROUP BY EXTRACT(MONTH FROM a.registrationDate)
            """)
    List<Object[]> countFirstVisitsByMonthForDoctor(
            @Param("doctorId") UUID doctorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("cancelled") BookingStatus cancelled);

    @Query("""
            SELECT a.patientId
            FROM AppointmentBooking a
            WHERE a.deleted = false
              AND a.assignedDoctorId = :doctorId
              AND a.bookingStatus <> :cancelled
            GROUP BY a.patientId
            HAVING MIN(a.registrationDate) >= :from AND MIN(a.registrationDate) < :to
            """)
    List<UUID> findNewPatientIdsForDoctorBetween(
            @Param("doctorId") UUID doctorId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("cancelled") BookingStatus cancelled);

}
