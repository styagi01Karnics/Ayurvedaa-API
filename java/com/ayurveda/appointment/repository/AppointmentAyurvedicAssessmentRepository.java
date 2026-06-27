package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentAyurvedicAssessment;

@Repository
public interface AppointmentAyurvedicAssessmentRepository
        extends JpaRepository<AppointmentAyurvedicAssessment, UUID> {

    Optional<AppointmentAyurvedicAssessment> findByBookingId(UUID bookingId);

    boolean existsByBookingId(UUID bookingId);

}