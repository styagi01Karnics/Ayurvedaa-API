package com.ayurveda.payment.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.payment.entity.PaymentLink;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, UUID> {

    Optional<PaymentLink> findByTokenAndDeletedFalse(String token);

    Optional<PaymentLink> findByIdAndDeletedFalse(UUID id);

    Optional<PaymentLink> findFirstByInvoiceIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
            UUID invoiceId, String status);

    Optional<PaymentLink> findFirstByInvoiceIdAndStatusInAndDeletedFalseOrderByCreatedAtDesc(
            UUID invoiceId, Collection<String> statuses);

    List<PaymentLink> findByInvoiceIdAndStatusAndDeletedFalse(UUID invoiceId, String status);

    List<PaymentLink> findByInvoiceIdAndStatusInAndDeletedFalse(UUID invoiceId, Collection<String> statuses);

    List<PaymentLink> findByStatusInAndExpiresAtBeforeAndDeletedFalse(
            Collection<String> statuses, LocalDateTime cutoff);

    @Query("""
            SELECT l FROM PaymentLink l
            WHERE l.deleted = false
              AND (
                    :status IS NULL
                    OR (UPPER(:status) = 'SHARED' AND UPPER(l.status) IN ('SHARED', 'OPEN'))
                    OR UPPER(l.status) = UPPER(:status)
                  )
              AND (:patientId IS NULL OR l.patientId = :patientId)
              AND (:invoiceId IS NULL OR l.invoiceId = :invoiceId)
              AND (
                    :search IS NULL OR :search = ''
                    OR LOWER(l.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(l.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(l.phone, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(COALESCE(l.invoiceNumber, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            ORDER BY l.createdAt DESC
            """)
    Page<PaymentLink> search(
            @Param("status") String status,
            @Param("patientId") UUID patientId,
            @Param("invoiceId") UUID invoiceId,
            @Param("search") String search,
            Pageable pageable);
}