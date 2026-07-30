package com.ayurveda.appointment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.enums.TherapyMasterStatus;

public interface TherapyMasterRepository
        extends JpaRepository<TherapyMaster, UUID> {

    List<TherapyMaster> findByStatusOrderByTherapyNameAsc(TherapyMasterStatus status);

}
