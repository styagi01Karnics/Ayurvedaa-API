package com.ayurveda.patient.util;

import java.time.Year;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ayurveda.patient.entity.Patient;
import com.ayurveda.patient.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientCodeGenerator {

    private static final String DISPLAY_PREFIX = "PT";
    private static final int DISPLAY_START = 458652;
    private static final int YEARLY_SEQ_START = 1;

    private final PatientRepository patientRepository;

    @Value("${patient.tenant-code:GAN}")
    private String tenantCode;

    public PatientIdentifiers generate() {
        return new PatientIdentifiers(nextDisplayId(), nextPatientCode());
    }

    private String nextDisplayId() {
        Optional<Patient> latest = patientRepository
                .findTopByPatientDisplayIdStartingWithOrderByPatientDisplayIdDesc(DISPLAY_PREFIX);

        int next = DISPLAY_START;
        if (latest.isPresent() && latest.get().getPatientDisplayId() != null) {
            String last = latest.get().getPatientDisplayId().replace(DISPLAY_PREFIX, "");
            next = Integer.parseInt(last) + 1;
        }
        return DISPLAY_PREFIX + next;
    }

    private String nextPatientCode() {
        String prefix = tenantCode.trim().toUpperCase() + Year.now().getValue() + "-";
        Optional<Patient> latest = patientRepository
                .findTopByPatientCodeStartingWithOrderByPatientCodeDesc(prefix);

        int next = YEARLY_SEQ_START;
        if (latest.isPresent() && latest.get().getPatientCode() != null) {
            String last = latest.get().getPatientCode();
            String serial = last.substring(last.lastIndexOf('-') + 1);
            next = Integer.parseInt(serial) + 1;
        }
        return prefix + String.format("%04d", next);
    }

    public record PatientIdentifiers(String patientDisplayId, String patientCode) {
    }

}
