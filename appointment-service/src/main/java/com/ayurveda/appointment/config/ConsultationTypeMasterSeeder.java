package com.ayurveda.appointment.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.enums.ConsultationTypeMasterStatus;
import com.ayurveda.appointment.repository.ConsultationTypeMasterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultationTypeMasterSeeder implements ApplicationRunner {

    private final ConsultationTypeMasterRepository consultationTypeMasterRepository;

    @Override
    public void run(ApplicationArguments args) {
        seedIfMissing("CONSULTATION");
        seedIfMissing("THERAPY");
    }

    private void seedIfMissing(String name) {
        if (consultationTypeMasterRepository.existsByNameIgnoreCaseAndDeletedFalse(name)) {
            return;
        }
        ConsultationTypeMaster entity = ConsultationTypeMaster.builder()
                .name(name)
                .status(ConsultationTypeMasterStatus.ACTIVE)
                .build();
        entity.setDeleted(false);
        consultationTypeMasterRepository.save(entity);
        log.info("Seeded consultation type master: {}", name);
    }

}
