package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentBooking;

@Repository
public interface AppointmentBookingRepository
        extends JpaRepository<AppointmentBooking, UUID> {
	
}