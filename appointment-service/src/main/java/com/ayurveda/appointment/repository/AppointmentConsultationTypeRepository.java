package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentConsultationType;

@Repository
public interface AppointmentConsultationTypeRepository
        extends JpaRepository<AppointmentConsultationType, UUID> {

    List<AppointmentConsultationType> findByBookingId(UUID bookingId);

    List<AppointmentConsultationType> findByBookingIdIn(Collection<UUID> bookingIds);

    void deleteByBookingId(UUID bookingId);

}