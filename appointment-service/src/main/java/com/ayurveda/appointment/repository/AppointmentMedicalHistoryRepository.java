package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentMedicalHistory;

@Repository
public interface AppointmentMedicalHistoryRepository
        extends JpaRepository<AppointmentMedicalHistory, UUID> {

    Optional<AppointmentMedicalHistory> findByPatientId(UUID patientId);

    boolean existsByPatientId(UUID patientId);

}