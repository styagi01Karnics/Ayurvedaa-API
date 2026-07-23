package com.ayurveda.attendance.repository;

import com.ayurveda.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {

    Optional<Attendance> findByIdAndDeletedFalse(UUID id);

    List<Attendance> findAllByDeletedFalse();

    List<Attendance> findAllByEmpIdAndDeletedFalseOrderByAttendanceDateDesc(String empId);

    List<Attendance> findAllByAttendanceDateAndDeletedFalse(LocalDate attendanceDate);

    boolean existsBySerialNumberAndDeletedFalse(String serialNumber);

    boolean existsByEmpIdAndAttendanceDateAndDeletedFalse(String empId, LocalDate attendanceDate);

}
