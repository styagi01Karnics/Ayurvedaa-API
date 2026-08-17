package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.PrescriptionTherapySuggestionItem;

@Repository
public interface PrescriptionTherapySuggestionItemRepository
        extends JpaRepository<PrescriptionTherapySuggestionItem, UUID> {

    List<PrescriptionTherapySuggestionItem> findAllByTherapySuggestionIdAndDeletedFalse(
            UUID therapySuggestionId);

    List<PrescriptionTherapySuggestionItem> findAllByTherapySuggestionIdInAndDeletedFalse(
            Collection<UUID> therapySuggestionIds);

}
