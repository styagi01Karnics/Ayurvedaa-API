package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentLifestyleInformation;

@Repository
public interface AppointmentLifestyleInformationRepository
        extends JpaRepository<AppointmentLifestyleInformation, UUID> {

    Optional<AppointmentLifestyleInformation> findByPatientId(UUID patientId);

    boolean existsByPatientId(UUID patientId);

}