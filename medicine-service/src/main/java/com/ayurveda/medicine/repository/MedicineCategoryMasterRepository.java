package com.ayurveda.medicine.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.medicine.entity.MedicineCategoryMaster;

public interface MedicineCategoryMasterRepository extends JpaRepository<MedicineCategoryMaster, UUID> {

    Optional<MedicineCategoryMaster> findByIdAndDeletedFalse(UUID id);

    Optional<MedicineCategoryMaster> findByCodeAndDeletedFalse(String code);

    boolean existsByCodeAndDeletedFalse(String code);

    List<MedicineCategoryMaster> findByDeletedFalseOrderByNameAsc();

    List<MedicineCategoryMaster> findByDeletedFalseAndActiveTrueOrderByNameAsc();

}
