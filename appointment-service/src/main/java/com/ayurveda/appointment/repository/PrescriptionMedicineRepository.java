package com.ayurveda.appointment.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ayurveda.appointment.entity.PrescriptionMedicine;

@Repository
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicine, UUID> {

    List<PrescriptionMedicine> findAllByPrescriptionIdAndDeletedFalse(UUID prescriptionId);

    List<PrescriptionMedicine> findAllByPrescriptionIdInAndDeletedFalse(Collection<UUID> prescriptionIds);

}
