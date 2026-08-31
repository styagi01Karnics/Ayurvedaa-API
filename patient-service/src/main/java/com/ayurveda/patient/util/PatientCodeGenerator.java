package com.ayurveda.patient.util;

import org.springframework.stereotype.Component;

import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;
import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientCodeGenerator {

    private final PatientRepository patientRepository;

    public String generate() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.PATIENT);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.PATIENT,
                patientRepository.findByPatientCodeStartingWith(prefix).stream()
                        .map(Patient::getPatientCode)
                        .toList());
    }

}
