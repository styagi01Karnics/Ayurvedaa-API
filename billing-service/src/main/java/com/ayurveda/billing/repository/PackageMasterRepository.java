package com.ayurveda.billing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.billing.entity.PackageMaster;
import com.ayurveda.billing.enums.PackageMasterStatus;

public interface PackageMasterRepository extends JpaRepository<PackageMaster, UUID> {

    @Query("""
            SELECT p FROM PackageMaster p
            WHERE p.id = :id
              AND COALESCE(p.deleted, false) = false
            """)
    Optional<PackageMaster> findByIdAndDeletedFalse(@Param("id") UUID id);

    @Query("""
            SELECT p FROM PackageMaster p
            WHERE LOWER(p.name) = LOWER(:name)
              AND COALESCE(p.deleted, false) = false
            """)
    Optional<PackageMaster> findByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

    @Query("""
            SELECT p FROM PackageMaster p
            WHERE COALESCE(p.deleted, false) = false
            ORDER BY p.name ASC
            """)
    List<PackageMaster> findAllByDeletedFalseOrderByNameAsc();

    @Query("""
            SELECT p FROM PackageMaster p
            WHERE p.status = :status
              AND COALESCE(p.deleted, false) = false
            ORDER BY p.name ASC
            """)
    List<PackageMaster> findAllByStatusAndDeletedFalseOrderByNameAsc(
            @Param("status") PackageMasterStatus status);

    @Query("""
            SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
            FROM PackageMaster p
            WHERE LOWER(p.name) = LOWER(:name)
              AND COALESCE(p.deleted, false) = false
            """)
    boolean existsByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

}
