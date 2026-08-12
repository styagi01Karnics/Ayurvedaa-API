package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.appointment.entity.DoshaMaster;

public interface DoshaMasterRepository extends JpaRepository<DoshaMaster, UUID> {

    @Query("""
            SELECT d FROM DoshaMaster d
            WHERE d.id = :id
              AND COALESCE(d.deleted, false) = false
            """)
    Optional<DoshaMaster> findByIdAndDeletedFalse(@Param("id") UUID id);

    @Query("""
            SELECT d FROM DoshaMaster d
            WHERE LOWER(d.name) = LOWER(:name)
              AND COALESCE(d.deleted, false) = false
            """)
    Optional<DoshaMaster> findByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

    @Query("""
            SELECT d FROM DoshaMaster d
            WHERE COALESCE(d.deleted, false) = false
            ORDER BY d.name ASC
            """)
    List<DoshaMaster> findAllByDeletedFalseOrderByNameAsc();

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM DoshaMaster d
            WHERE LOWER(d.name) = LOWER(:name)
              AND COALESCE(d.deleted, false) = false
            """)
    boolean existsByNameIgnoreCaseAndDeletedFalse(@Param("name") String name);

}
