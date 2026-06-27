package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentTreatmentPlan;

@Repository
public interface AppointmentTreatmentPlanRepository
        extends JpaRepository<AppointmentTreatmentPlan, UUID> {

    Optional<AppointmentTreatmentPlan> findByBookingId(UUID bookingId);

    boolean existsByBookingId(UUID bookingId);

}