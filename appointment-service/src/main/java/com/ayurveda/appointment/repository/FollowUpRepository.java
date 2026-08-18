package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.FollowUp;

@Repository
public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

    Optional<FollowUp> findByIdAndDeletedFalse(UUID id);

    List<FollowUp> findAllByDeletedFalseOrderByAppointmentDateAsc();

    List<FollowUp> findAllByPatientIdAndDeletedFalseOrderByAppointmentDateAsc(UUID patientId);

    @Query("""
            SELECT DISTINCT f.sourceBookingId
            FROM FollowUp f
            WHERE f.deleted = false
              AND f.sourceBookingId IS NOT NULL
              AND f.sourceBookingId IN :bookingIds
            """)
    List<UUID> findSourceBookingIdsWithFollowUp(@Param("bookingIds") Collection<UUID> bookingIds);

}
