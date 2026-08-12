package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.enums.ConsultationTypeMasterStatus;

public interface ConsultationTypeMasterRepository extends JpaRepository<ConsultationTypeMaster, UUID> {

    @Query("""
            SELECT c FROM ConsultationTypeMaster c
            WHERE c.id = :id
              AND COALESCE(c.deleted, false) = false
            """)
    Optional<ConsultationTypeMaster> findByIdAndDeletedFalse(@Param("id") UUID id);

    @Query("""
            SELECT c FROM ConsultationTypeMaster c
            WHERE LOWER(c.name) = LOWER(:name)
              AND COALESCE(c.deleted, false) = false
            """)
    Optional<ConsultationTypeMaster> findByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

    @Query("""
            SELECT c FROM ConsultationTypeMaster c
            WHERE COALESCE(c.deleted, false) = false
            ORDER BY c.name ASC
            """)
    List<ConsultationTypeMaster> findAllByDeletedFalseOrderByNameAsc();

    @Query("""
            SELECT c FROM ConsultationTypeMaster c
            WHERE c.status = :status
              AND COALESCE(c.deleted, false) = false
            ORDER BY c.name ASC
            """)
    List<ConsultationTypeMaster> findAllByStatusAndDeletedFalseOrderByNameAsc(
            @Param("status") ConsultationTypeMasterStatus status);

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM ConsultationTypeMaster c
            WHERE LOWER(c.name) = LOWER(:name)
              AND COALESCE(c.deleted, false) = false
            """)
    boolean existsByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

    @Query("""
            SELECT c FROM ConsultationTypeMaster c
            WHERE c.id IN :ids
              AND COALESCE(c.deleted, false) = false
            """)
    List<ConsultationTypeMaster> findByIdInAndDeletedFalse(@Param("ids") Collection<UUID> ids);

}
