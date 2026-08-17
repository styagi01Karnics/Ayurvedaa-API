package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.PrescriptionTherapySuggestion;

@Repository
public interface PrescriptionTherapySuggestionRepository
        extends JpaRepository<PrescriptionTherapySuggestion, UUID> {

    List<PrescriptionTherapySuggestion> findAllByPrescriptionIdAndDeletedFalse(UUID prescriptionId);

    List<PrescriptionTherapySuggestion> findAllByPrescriptionIdInAndDeletedFalse(
            Collection<UUID> prescriptionIds);

}
