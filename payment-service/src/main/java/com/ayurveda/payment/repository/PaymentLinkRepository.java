package com.ayurveda.payment.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ayurveda.payment.entity.PaymentLink;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, UUID>, JpaSpecificationExecutor<PaymentLink> {

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

    Page<PaymentLink> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
}
