package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.appointment.entity.DoshaMaster;

public interface DoshaMasterRepository extends JpaRepository<DoshaMaster, UUID> {

    Optional<DoshaMaster> findByIdAndDeletedFalse(UUID id);

    Optional<DoshaMaster> findByNameIgnoreCaseAndDeletedFalse(String name);

    List<DoshaMaster> findAllByDeletedFalseOrderByNameAsc();

    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);

}
