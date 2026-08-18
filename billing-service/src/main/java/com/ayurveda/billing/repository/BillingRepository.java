package com.ayurveda.billing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.billing.entity.Billing;
import com.ayurveda.billing.enums.BillingStatus;

public interface BillingRepository extends JpaRepository<Billing, UUID> {

    Optional<Billing> findByIdAndDeletedFalse(UUID id);

    List<Billing> findAllByDeletedFalseOrderByBillingDateDescCreatedAtDesc();

    List<Billing> findAllByPatientIdAndDeletedFalseOrderByBillingDateDescCreatedAtDesc(UUID patientId);

    @Query("""
            SELECT b FROM Billing b
            WHERE b.deleted = false
              AND (:status IS NULL OR b.status = :status)
            ORDER BY b.billingDate DESC, b.createdAt DESC
            """)
    List<Billing> findAllByStatusOptional(@Param("status") BillingStatus status);

}
