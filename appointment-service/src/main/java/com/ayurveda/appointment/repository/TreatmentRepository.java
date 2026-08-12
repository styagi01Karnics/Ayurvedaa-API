package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.Treatment;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {

    Optional<Treatment> findByIdAndDeletedFalse(UUID id);

    List<Treatment> findAllByDeletedFalseOrderByStartDateDesc();

    List<Treatment> findAllByPatientIdAndDeletedFalseOrderByStartDateDesc(UUID patientId);

}
