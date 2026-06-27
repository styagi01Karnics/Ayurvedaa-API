package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.AppointmentTherapyRecommendation;

@Repository
public interface AppointmentTherapyRecommendationRepository
        extends JpaRepository<AppointmentTherapyRecommendation, UUID> {

    List<AppointmentTherapyRecommendation> findByAppointmentTherapyId(UUID appointmentTherapyId);

    List<AppointmentTherapyRecommendation> findByTherapyMasterId(UUID therapyMasterId);

}