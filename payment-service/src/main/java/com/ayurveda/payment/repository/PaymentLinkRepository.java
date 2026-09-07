package com.ayurveda.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.payment.entity.PaymentLink;

public interface PaymentLinkRepository extends JpaRepository<PaymentLink, UUID> {

    Optional<PaymentLink> findByTokenAndDeletedFalse(String token);

    Optional<PaymentLink> findByIdAndDeletedFalse(UUID id);

    Optional<PaymentLink> findFirstByInvoiceIdAndStatusAndDeletedFalseOrderByCreatedAtDesc(
            UUID invoiceId, String status);

    List<PaymentLink> findByInvoiceIdAndStatusAndDeletedFalse(UUID invoiceId, String status);
}
