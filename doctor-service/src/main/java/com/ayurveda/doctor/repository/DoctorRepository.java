package com.ayurveda.doctor.repository;

import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.enums.DoctorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByIdAndDeletedFalse(UUID id);

    List<Doctor> findAllByDeletedFalse();

    List<Doctor> findAllByStatusAndDeletedFalse(DoctorStatus status);

    boolean existsByDoctorCodeAndDeletedFalse(String doctorCode);

    boolean existsByEmailAndDeletedFalse(String email);

    Optional<Doctor> findTopByDoctorCodeStartingWithOrderByDoctorCodeDesc(String prefix);

    List<Doctor> findByDoctorCodeStartingWith(String prefix);

}
