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

    Optional<TherapyMaster> findByTherapyName(String therapyName);

    boolean existsByTherapyName(String therapyName);

    Optional<TherapyMaster> findTopByOrderByTherapyCodeDesc();

    List<TherapyMaster> findByCategoryId(UUID categoryId);

}