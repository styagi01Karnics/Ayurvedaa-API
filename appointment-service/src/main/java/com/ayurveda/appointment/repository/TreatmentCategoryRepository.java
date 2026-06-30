package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.TreatmentCategoryMaster;

@Repository
public interface TreatmentCategoryRepository
        extends JpaRepository<TreatmentCategoryMaster, UUID> {

    Optional<TreatmentCategoryMaster> findByCategoryName(String categoryName);

    boolean existsByCategoryName(String categoryName);

    Optional<TreatmentCategoryMaster> findTopByOrderByCategoryCodeDesc();

}