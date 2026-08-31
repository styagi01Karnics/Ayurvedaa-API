package com.ayurveda.patient.repository;

import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByIdAndDeletedFalse(UUID id);

    List<Patient> findAllByDeletedFalse();

    boolean existsByPatientCodeAndDeletedFalse(String patientCode);

    boolean existsByPatientDisplayIdAndDeletedFalse(String patientDisplayId);

    Optional<Patient> findTopByPatientDisplayIdStartingWithOrderByPatientDisplayIdDesc(String prefix);

    Optional<Patient> findTopByPatientCodeStartingWithOrderByPatientCodeDesc(String prefix);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByMobileNumberAndDeletedFalse(String mobileNumber);

    long countByDeletedFalse();

    long countByDeletedFalseAndStatus(PatientStatus status);

}
