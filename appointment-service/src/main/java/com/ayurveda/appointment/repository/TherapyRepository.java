package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.TherapyMaster;

@Repository
public interface TherapyRepository
        extends JpaRepository<TherapyMaster, UUID> {

    Optional<TherapyMaster> findByIdAndDeletedFalse(UUID id);

    List<TherapyMaster> findAllByDeletedFalse();

    Optional<TherapyMaster> findByTherapyName(String therapyName);

    boolean existsByTherapyNameAndDeletedFalse(String therapyName);

    Optional<TherapyMaster> findTopByOrderByTherapyCodeDesc();

    List<TherapyMaster> findByCategoryIdAndDeletedFalse(UUID categoryId);

    List<TherapyMaster> findByIdInAndDeletedFalse(List<UUID> ids);

}
