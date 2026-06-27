package com.ayurveda.appointment.util;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.repository.TherapyRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TherapyCodeGenerator {

    private final TherapyRepository therapyRepository;

    public String generateTherapyCode() {

        Optional<TherapyMaster> latestTherapy =
                therapyRepository.findTopByOrderByTherapyCodeDesc();

        if (latestTherapy.isEmpty()
                || latestTherapy.get().getTherapyCode() == null) {
            return "TH001";
        }

        String lastCode = latestTherapy.get().getTherapyCode();

        int nextNumber = Integer.parseInt(lastCode.substring(2)) + 1;

        return String.format("TH%03d", nextNumber);
    }
}