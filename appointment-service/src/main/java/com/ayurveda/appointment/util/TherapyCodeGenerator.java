package com.ayurveda.appointment.util;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TherapyCodeGenerator {

    private final TherapyRepository therapyRepository;

    public String generateTherapyCode() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.THERAPY);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.THERAPY,
                therapyRepository.findByTherapyCodeStartingWith(prefix).stream()
                        .map(TherapyMaster::getTherapyCode)
                        .toList());
    }

}
