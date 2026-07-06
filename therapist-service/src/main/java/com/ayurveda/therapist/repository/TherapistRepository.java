package com.ayurveda.therapist.repository;

import com.ayurveda.therapist.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TherapistRepository extends JpaRepository<Therapist, UUID> {

    Optional<Therapist> findByIdAndDeletedFalse(UUID id);

    List<Therapist> findAllByDeletedFalse();

    boolean existsByTherapistCodeAndDeletedFalse(String therapistCode);

    boolean existsByEmailAndDeletedFalse(String email);

}
