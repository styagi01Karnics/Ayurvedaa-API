package com.ayurveda.attendance.repository;

import com.ayurveda.attendance.entity.EmployeeAttendanceMaster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeAttendanceMasterRepository extends JpaRepository<EmployeeAttendanceMaster, UUID> {

    Optional<EmployeeAttendanceMaster> findByIdAndDeletedFalse(UUID id);

    Optional<EmployeeAttendanceMaster> findByEmpIdAndDeletedFalse(String empId);

    List<EmployeeAttendanceMaster> findAllByDeletedFalse();

    boolean existsByEmpIdAndDeletedFalse(String empId);

}
