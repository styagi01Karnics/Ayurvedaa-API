package com.ayurveda.billing.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.billing.entity.Invoice;
import com.ayurveda.billing.enums.InvoiceStatus;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByIdAndDeletedFalse(UUID id);

    Optional<Invoice> findByInvoiceNumberAndDeletedFalse(String invoiceNumber);

    Optional<Invoice> findTopByOrderByInvoiceNumberDesc();

    List<Invoice> findByInvoiceNumberStartingWith(String prefix);

    @Query("""
            SELECT i FROM Invoice i
            WHERE i.deleted = false
              AND (
                    :patientId IS NULL
                    OR i.patientId = :patientId
                  )
              AND (
                    :patientSearch IS NULL OR :patientSearch = ''
                    OR LOWER(i.patientName) LIKE LOWER(CONCAT('%', :patientSearch, '%'))
                    OR LOWER(i.invoiceNumber) LIKE LOWER(CONCAT('%', :patientSearch, '%'))
                  )
              AND (:status IS NULL OR i.status = :status)
            ORDER BY i.invoiceDate DESC, i.createdAt DESC
            """)
    List<Invoice> search(
            @Param("patientId") UUID patientId,
            @Param("patientSearch") String patientSearch,
            @Param("status") InvoiceStatus status);

    @Query("""
            SELECT i FROM Invoice i
            WHERE i.deleted = false
              AND (
                    :serviceType IS NULL OR :serviceType = ''
                    OR LOWER(COALESCE(i.packageType, '')) LIKE LOWER(CONCAT('%', :serviceType, '%'))
                    OR LOWER(COALESCE(i.billSections, '')) LIKE LOWER(CONCAT('%', :serviceType, '%'))
                  )
              AND (:dateCreated IS NULL OR i.invoiceDate = :dateCreated)
            ORDER BY i.invoiceDate DESC, i.createdAt DESC
            """)
    List<Invoice> searchSales(
            @Param("serviceType") String serviceType,
            @Param("dateCreated") LocalDate dateCreated);

    @Query("""
            SELECT COALESCE(SUM(i.totalAmount), 0)
            FROM Invoice i
            WHERE i.deleted = false
              AND i.invoiceDate >= :fromDate
              AND i.invoiceDate <= :toDate
            """)
    BigDecimal sumTotalAmountBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT COALESCE(SUM(i.paidAmount), 0)
            FROM Invoice i
            WHERE i.deleted = false
              AND i.invoiceDate >= :fromDate
              AND i.invoiceDate <= :toDate
            """)
    BigDecimal sumPaidAmountBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT COALESCE(SUM(i.leftAmount), 0)
            FROM Invoice i
            WHERE i.deleted = false
              AND i.invoiceDate >= :fromDate
              AND i.invoiceDate <= :toDate
            """)
    BigDecimal sumLeftAmountBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

    @Query("""
            SELECT COUNT(i)
            FROM Invoice i
            WHERE i.deleted = false
              AND i.invoiceDate >= :fromDate
              AND i.invoiceDate <= :toDate
            """)
    long countBetween(
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate);

}
