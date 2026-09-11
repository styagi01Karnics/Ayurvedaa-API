package com.ayurveda.patient.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.enums.PatientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByIdAndDeletedFalse(UUID id);

    List<Patient> findAllByDeletedFalse();

    Page<Patient> findAllByDeletedFalse(Pageable pageable);

    boolean existsByPatientCodeAndDeletedFalse(String patientCode);

    Optional<Patient> findTopByPatientCodeStartingWithOrderByPatientCodeDesc(String prefix);

    List<Patient> findByPatientCodeStartingWith(String prefix);

    boolean existsByEmailAndDeletedFalse(String email);

    boolean existsByMobileNumberAndDeletedFalse(String mobileNumber);

    long countByDeletedFalse();

    long countByDeletedFalseAndStatus(PatientStatus status);

    @Query("""
            SELECT EXTRACT(MONTH FROM p.createdAt), COUNT(p)
            FROM Patient p
            WHERE p.deleted = false
              AND p.createdAt >= :from
              AND p.createdAt < :to
            GROUP BY EXTRACT(MONTH FROM p.createdAt)
            """)
    List<Object[]> countCreatedByMonth(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);

}
