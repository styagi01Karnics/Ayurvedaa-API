package com.ayurveda.medicine.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.medicine.entity.Medicine;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStockStatus;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    Optional<Medicine> findByIdAndDeletedFalse(UUID id);

    @Query("""
            SELECT m FROM Medicine m
            WHERE m.deleted = false
              AND (:medicineName IS NULL OR :medicineName = ''
                   OR LOWER(m.medicineName) LIKE LOWER(CONCAT('%', :medicineName, '%')))
              AND (:category IS NULL OR m.category = :category)
              AND (:stockStatus IS NULL OR m.stockStatus = :stockStatus)
            ORDER BY m.medicineName ASC
            """)
    List<Medicine> search(
            @Param("medicineName") String medicineName,
            @Param("category") MedicineCategory category,
            @Param("stockStatus") MedicineStockStatus stockStatus);

    @Query("""
            SELECT DISTINCT m.manufacturer FROM Medicine m
            WHERE m.deleted = false
            ORDER BY m.manufacturer ASC
            """)
    List<String> findDistinctManufacturers();

    @Query("""
            SELECT m FROM Medicine m
            WHERE m.deleted = false
            ORDER BY m.medicineName ASC
            """)
    List<Medicine> findAllNamesOrdered();

    @Query("""
            SELECT COALESCE(SUM(m.quantity), 0) FROM Medicine m
            WHERE m.deleted = false
            """)
    long sumTotalStock();

    @Query("""
            SELECT COALESCE(SUM(m.quantity), 0) FROM Medicine m
            WHERE m.deleted = false
              AND m.category = :category
            """)
    long sumStockByCategory(@Param("category") MedicineCategory category);

    @Query("""
            SELECT COUNT(m) FROM Medicine m
            WHERE m.deleted = false
              AND m.category = :category
            """)
    long countByCategory(@Param("category") MedicineCategory category);

    List<Medicine> findByStockStatusAndDeletedFalseOrderByQuantityAsc(MedicineStockStatus stockStatus);

    long countByStockStatusAndDeletedFalse(MedicineStockStatus stockStatus);

}
