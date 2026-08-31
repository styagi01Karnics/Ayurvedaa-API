package com.ayurveda.therapist.util;

import org.springframework.stereotype.Component;

import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.repository.TherapistRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TherapistCodeGenerator {

    private final TherapistRepository therapistRepository;

    public String generate() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.THERAPIST);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.THERAPIST,
                therapistRepository.findByTherapistCodeStartingWith(prefix).stream()
                        .map(Therapist::getTherapistCode)
                        .toList());
    }

}
