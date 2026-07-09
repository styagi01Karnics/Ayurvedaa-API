package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentSystemicExamination;

@Repository
public interface AppointmentSystemicExaminationRepository
        extends JpaRepository<AppointmentSystemicExamination, UUID> {

    Optional<AppointmentSystemicExamination> findByPatientId(UUID patientId);

    boolean existsByPatientId(UUID patientId);

}