package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentTherapy;

@Repository
public interface AppointmentTherapyRepository
        extends JpaRepository<AppointmentTherapy, UUID> {

    Optional<AppointmentTherapy> findByBookingId(UUID bookingId);

}