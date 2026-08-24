package com.ayurveda.auth.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.entity.UiPage;
import com.ayurveda.auth.repository.UiPageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * UI modules from Role Management Figma (hospital admin assigns these to roles).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UiPageSeeder implements ApplicationRunner {

    private final UiPageRepository uiPageRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<UiPage> defaults = List.of(
                page("DASHBOARD", "Dashboard", "Home dashboard", "HOME", 10),
                page("PATIENTS", "Patients", "Patient management", "CLINICAL", 20),
                page("DOCTORS", "Doctors", "Doctor management", "CLINICAL", 30),
                page("APPOINTMENTS", "Appointments", "Appointment booking", "CLINICAL", 40),
                page("TREATMENTS", "Treatments", "Treatment plans", "CLINICAL", 50),
                page("MEDICINES", "Medicines", "Medicine catalog", "PHARMACY", 60),
                page("SALES", "Sales", "Sales and pharmacy billing", "SALES", 70),
                page("ACTIVITY_LOG", "Activity Log", "Activity logs", "ADMIN", 80),
                page("BILLING", "Billing", "Invoices and payments", "BILLING", 90),
                page("SETTINGS", "Settings", "Hospital settings", "ADMIN", 100)
        );

        for (UiPage page : defaults) {
            if (!uiPageRepository.existsByPageCodeIgnoreCaseAndDeletedFalse(page.getPageCode())) {
                uiPageRepository.save(page);
            }
        }
        log.info("UI page catalog seeded/verified (hospital Role Management modules)");
    }

    private static UiPage page(
            String code, String name, String description, String module, int sortOrder) {
        return UiPage.builder()
                .pageCode(code)
                .pageName(name)
                .description(description)
                .module(module)
                .sortOrder(sortOrder)
                .build();
    }

}
