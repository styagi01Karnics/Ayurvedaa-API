package com.ayurveda.appointment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.DoctorMaster;

@Repository
public interface DoctorMasterRepository
        extends JpaRepository<DoctorMaster, UUID> {

    Optional<DoctorMaster> findByDoctorCode(String doctorCode);

    boolean existsByDoctorCode(String doctorCode);

    boolean existsByDoctorName(String doctorName);

}