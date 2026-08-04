package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.FollowUp;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

    Optional<FollowUp> findByIdAndDeletedFalse(UUID id);

    List<FollowUp> findAllByDeletedFalseOrderByAppointmentDateAsc();

    List<FollowUp> findAllByPatientIdAndDeletedFalseOrderByAppointmentDateAsc(UUID patientId);

}
