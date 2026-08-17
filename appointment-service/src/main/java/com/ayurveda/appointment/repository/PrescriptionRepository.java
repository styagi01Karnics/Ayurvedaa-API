package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByIdAndDeletedFalse(UUID id);

    List<Prescription> findAllByPatientIdAndDeletedFalseOrderByCreatedAtDesc(UUID patientId);

}
