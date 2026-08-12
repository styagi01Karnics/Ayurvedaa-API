package com.ayurveda.billing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.billing.entity.PatientPackage;

@Repository
public interface PatientPackageRepository extends JpaRepository<PatientPackage, UUID> {

    Optional<PatientPackage> findByIdAndDeletedFalse(UUID id);

    List<PatientPackage> findAllByDeletedFalseOrderByValidityDesc();

    List<PatientPackage> findAllByPatientIdAndDeletedFalseOrderByValidityDesc(UUID patientId);

}
