package com.ayurveda.therapist.repository;

import com.ayurveda.therapist.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TherapistRepository extends JpaRepository<Therapist, UUID> {

    Optional<Therapist> findByIdAndDeletedFalse(UUID id);

    List<Therapist> findAllByDeletedFalse();

    boolean existsByTherapistCodeAndDeletedFalse(String therapistCode);

    boolean existsByEmailAndDeletedFalse(String email);

    Optional<Therapist> findTopByTherapistCodeStartingWithOrderByTherapistCodeDesc(String prefix);

    List<Therapist> findByTherapistCodeStartingWith(String prefix);

    @Query("""
            SELECT DISTINCT t FROM Therapist t
            JOIN t.assignedTherapyIds therapyId
            WHERE t.deleted = false
              AND therapyId IN :therapyIds
            ORDER BY t.therapistName ASC
            """)
    List<Therapist> findByAssignedTherapyIds(
            @Param("therapyIds") Collection<UUID> therapyIds);

}
