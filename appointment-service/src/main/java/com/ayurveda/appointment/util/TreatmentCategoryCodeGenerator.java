package com.ayurveda.appointment.util;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TreatmentCategoryCodeGenerator {

    private final TreatmentCategoryRepository treatmentCategoryRepository;

    public String generateCategoryCode() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.TREATMENT_CATEGORY);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.TREATMENT_CATEGORY,
                treatmentCategoryRepository.findByCategoryCodeStartingWith(prefix).stream()
                        .map(TreatmentCategoryMaster::getCategoryCode)
                        .toList());
    }

}
