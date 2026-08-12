package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.appointment.entity.TreatmentPlanMaster;
import com.ayurveda.appointment.enums.TreatmentPlanMasterStatus;

public interface TreatmentPlanMasterRepository extends JpaRepository<TreatmentPlanMaster, UUID> {

    @Query("""
            SELECT t FROM TreatmentPlanMaster t
            WHERE t.id = :id
              AND COALESCE(t.deleted, false) = false
            """)
    Optional<TreatmentPlanMaster> findByIdAndDeletedFalse(@Param("id") UUID id);

    @Query("""
            SELECT t FROM TreatmentPlanMaster t
            WHERE LOWER(t.name) = LOWER(:name)
              AND COALESCE(t.deleted, false) = false
            """)
    Optional<TreatmentPlanMaster> findByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

    @Query("""
            SELECT t FROM TreatmentPlanMaster t
            WHERE COALESCE(t.deleted, false) = false
            ORDER BY t.name ASC
            """)
    List<TreatmentPlanMaster> findAllByDeletedFalseOrderByNameAsc();

    @Query("""
            SELECT t FROM TreatmentPlanMaster t
            WHERE t.status = :status
              AND COALESCE(t.deleted, false) = false
            ORDER BY t.name ASC
            """)
    List<TreatmentPlanMaster> findAllByStatusAndDeletedFalseOrderByNameAsc(
            @Param("status") TreatmentPlanMasterStatus status);

    @Query("""
            SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
            FROM TreatmentPlanMaster t
            WHERE LOWER(t.name) = LOWER(:name)
              AND COALESCE(t.deleted, false) = false
            """)
    boolean existsByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

}
