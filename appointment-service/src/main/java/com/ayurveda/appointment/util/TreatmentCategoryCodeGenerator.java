package com.ayurveda.appointment.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TreatmentCategoryCodeGenerator {

    private final TreatmentCategoryRepository treatmentCategoryRepository;

    public String generateCategoryCode() {

        Optional<TreatmentCategoryMaster> latestCategory =
                treatmentCategoryRepository.findTopByOrderByCategoryCodeDesc();

        if (latestCategory.isEmpty()
                || latestCategory.get().getCategoryCode() == null) {
            return "TC001";
        }

        String lastCode = latestCategory.get().getCategoryCode();

        int nextNumber = Integer.parseInt(lastCode.substring(2)) + 1;

        return String.format("TC%03d", nextNumber);
    }
}