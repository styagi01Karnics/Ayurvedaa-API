package com.ayurveda.payment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.payment.entity.PaymentTransaction;

public interface PaymentRepository extends JpaRepository<PaymentTransaction, UUID> {

    Optional<PaymentTransaction> findByIdAndDeletedFalse(UUID id);

    Optional<PaymentTransaction> findByPayuTxnIdAndDeletedFalse(String payuTxnId);

    Optional<PaymentTransaction> findByPaymentCodeAndDeletedFalse(String paymentCode);

    List<PaymentTransaction> findByDeletedFalseOrderByCreatedAtDesc();

    List<PaymentTransaction> findByInvoiceIdAndDeletedFalseOrderByCreatedAtDesc(UUID invoiceId);

    @Query("""
            SELECT p.paymentCode FROM PaymentTransaction p
            WHERE p.paymentCode LIKE CONCAT(:prefix, '%')
            """)
    List<String> findCodesByPrefix(@Param("prefix") String prefix);
}
