package com.ayurveda.auth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.entity.TenantRolePage;
import com.ayurveda.auth.entity.UiPage;
import com.ayurveda.auth.repository.TenantRolePageRepository;
import com.ayurveda.auth.repository.TenantRoleRepository;
import com.ayurveda.auth.repository.UiPageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantBootstrapService {

    public static final String HOSPITAL_ADMIN_ROLE_CODE = "HOSPITAL_ADMIN";
    public static final String DOCTOR_ROLE_CODE = "DOCTOR";
    public static final String RECEPTIONIST_ROLE_CODE = "RECEPTIONIST";

    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePageRepository tenantRolePageRepository;
    private final UiPageRepository uiPageRepository;

    @Transactional
    public TenantRole ensureHospitalAdminRole(Tenant hospital) {
        seedDefaultRoleTemplates(hospital);
        return tenantRoleRepository
                .findByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(
                        hospital.getId(), HOSPITAL_ADMIN_ROLE_CODE)
                .orElseThrow();
    }

    /** Seeds HOSPITAL_ADMIN (all pages), DOCTOR and RECEPTIONIST templates when missing. */
    @Transactional
    public void seedDefaultRoleTemplates(Tenant hospital) {
        List<UiPage> allPages = uiPageRepository.findAllByDeletedFalseOrderBySortOrderAsc();

        ensureRole(
                hospital,
                HOSPITAL_ADMIN_ROLE_CODE,
                "Hospital Admin",
                "Full access to all hospital UI pages",
                true,
                allPages.stream().map(UiPage::getPageCode).toList());

        ensureRole(
                hospital,
                DOCTOR_ROLE_CODE,
                "Doctor",
                "Clinical modules for doctors",
                true,
                List.of("DASHBOARD", "PATIENTS", "DOCTORS", "APPOINTMENTS", "TREATMENTS"));

        ensureRole(
                hospital,
                RECEPTIONIST_ROLE_CODE,
                "Receptionist",
                "Front-desk patient and appointment access",
                true,
                List.of("DASHBOARD", "PATIENTS", "APPOINTMENTS", "BILLING"));
    }

    private void ensureRole(
            Tenant hospital,
            String roleCode,
            String roleName,
            String description,
            boolean systemRole,
            List<String> pageCodes) {

        if (tenantRoleRepository.existsByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(
                hospital.getId(), roleCode)) {
            return;
        }

        TenantRole role = TenantRole.builder()
                .tenant(hospital)
                .roleCode(roleCode)
                .roleName(roleName)
                .description(description)
                .systemRole(systemRole)
                .active(true)
                .build();
        TenantRole saved = tenantRoleRepository.save(role);

        List<UiPage> pages = uiPageRepository.findAllByPageCodeInAndDeletedFalse(pageCodes);
        // If PRESCRIPTIONS page not in catalog, skip missing codes quietly
        for (UiPage page : pages) {
            tenantRolePageRepository.save(TenantRolePage.builder()
                    .tenantRole(saved)
                    .uiPage(page)
                    .build());
        }
        log.info("Seeded role {} for hospital {}", roleCode, hospital.getTenantCode());
    }

}
