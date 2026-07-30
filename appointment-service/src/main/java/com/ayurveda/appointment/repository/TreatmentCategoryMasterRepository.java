package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TreatmentCategoryStatus;

public interface TreatmentCategoryMasterRepository
        extends JpaRepository<TreatmentCategoryMaster, UUID> {

    List<TreatmentCategoryMaster> findByStatusOrderByCategoryNameAsc(TreatmentCategoryStatus status);

}
