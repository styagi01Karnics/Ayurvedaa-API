package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentPhysicalExamination;

@Repository
public interface AppointmentPhysicalExaminationRepository
        extends JpaRepository<AppointmentPhysicalExamination, UUID> {

    Optional<AppointmentPhysicalExamination> findByPatientId(UUID patientId);

    boolean existsByPatientId(UUID patientId);

}