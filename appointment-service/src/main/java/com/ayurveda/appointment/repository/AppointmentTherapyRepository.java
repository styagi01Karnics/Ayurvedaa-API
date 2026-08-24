package com.ayurveda.appointment.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.enums.TherapyStatus;

@Repository
public interface AppointmentTherapyRepository
        extends JpaRepository<AppointmentTherapy, UUID> {

    List<AppointmentTherapy> findAllByPatientId(UUID patientId);

    Optional<AppointmentTherapy> findByIdAndDeletedFalse(UUID id);

    @Query("""
            SELECT t FROM AppointmentTherapy t
            WHERE t.assignedTherapistId = :therapistId
              AND t.scheduleDate = :date
              AND t.deleted = false
              AND t.therapyStatus <> :cancelled
            ORDER BY t.scheduleTime ASC
            """)
    List<AppointmentTherapy> findByTherapistAndDateExcludingCancelled(
            @Param("therapistId") UUID therapistId,
            @Param("date") LocalDate date,
            @Param("cancelled") TherapyStatus cancelled);

    @Query("""
            SELECT t FROM AppointmentTherapy t
            WHERE t.assignedTherapistId = :therapistId
              AND t.scheduleDate = :date
              AND t.deleted = false
              AND t.therapyStatus <> :cancelled
            ORDER BY t.scheduleTime ASC
            """)
    Page<AppointmentTherapy> findByTherapistAndDateExcludingCancelled(
            @Param("therapistId") UUID therapistId,
            @Param("date") LocalDate date,
            @Param("cancelled") TherapyStatus cancelled,
            Pageable pageable);

}
