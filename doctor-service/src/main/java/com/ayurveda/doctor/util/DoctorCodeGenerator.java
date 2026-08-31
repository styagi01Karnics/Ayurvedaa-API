package com.ayurveda.doctor.util;

import org.springframework.stereotype.Component;

import com.ayurveda.common.util.BusinessCodeGenerator;
import com.ayurveda.common.util.BusinessCodeTypes;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DoctorCodeGenerator {

    private final DoctorRepository doctorRepository;

    public String generate() {
        String prefix = BusinessCodeGenerator.prefix(BusinessCodeTypes.DOCTOR);
        return BusinessCodeGenerator.next(
                BusinessCodeTypes.DOCTOR,
                doctorRepository.findByDoctorCodeStartingWith(prefix).stream()
                        .map(Doctor::getDoctorCode)
                        .toList());
    }

}
