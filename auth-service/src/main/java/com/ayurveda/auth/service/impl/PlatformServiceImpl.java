package com.ayurveda.auth.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.request.BootstrapSuperAdminRequest;
import com.ayurveda.auth.dto.request.CreateHospitalAdminRequest;
import com.ayurveda.auth.dto.request.OnboardHospitalRequest;
import com.ayurveda.auth.dto.request.UpdateHospitalRequest;
import com.ayurveda.auth.dto.request.UpdateHospitalStatusRequest;
import com.ayurveda.auth.dto.response.HospitalOnboardResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.AuthUserRepository;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.auth.security.AuthPrincipal;
import com.ayurveda.auth.service.HospitalOnboardActivityLogger;
import com.ayurveda.auth.service.PageAccessService;
import com.ayurveda.auth.service.PlatformService;
import com.ayurveda.auth.service.SchemaProvisioningService;
import com.ayurveda.auth.service.TenantBootstrapService;
import com.ayurveda.auth.service.TenantCodeGenerator;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.constant.AppConstants;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.DuplicateResourceException;
import com.ayurveda.common.exception.ForbiddenException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlatformServiceImpl implements PlatformService {

    public static final String PLATFORM_TENANT_CODE = "PLATFORM";
    public static final String PLATFORM_SCHEMA = "public";

    private final TenantRepository tenantRepository;
    private final AuthUserRepository authUserRepository;
    private final TenantBootstrapService tenantBootstrapService;
    private final SchemaProvisioningService schemaProvisioningService;
    private final TenantCodeGenerator tenantCodeGenerator;
    private final PageAccessService pageAccessService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;
    private final HospitalOnboardActivityLogger hospitalOnboardActivityLogger;

    @Override
    public ApiResponse<UserResponse> bootstrapSuperAdmin(BootstrapSuperAdminRequest request) {
        if (authUserRepository.existsByRoleAndDeletedFalse(UserRole.SUPER_ADMIN)) {
            throw new BadRequestException(AuthMessages.SUPER_ADMIN_ALREADY_EXISTS);
        }

        Tenant platform = tenantRepository.findFirstByPlatformTrueAndDeletedFalse()
                .orElseGet(this::createPlatformTenant);

        String email = request.getEmail().trim().toLowerCase();

        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(platform.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + email);
        }

        AuthUser superAdmin = AuthUser.builder()
                .tenant(platform)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.SUPER_ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(superAdmin);
        log.info("Bootstrapped platform SUPER_ADMIN {}", email);

        return ApiResponse.success(
                AuthMessages.PLATFORM_BOOTSTRAPPED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageAccessService.resolvePageCodes(saved)));
    }

    @Override
    @Transactional(noRollbackFor = { BadRequestException.class, DuplicateResourceException.class })
    public ApiResponse<HospitalOnboardResponse> onboardHospital(OnboardHospitalRequest request) {
        requireSuperAdmin();

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(AuthMessages.NEW_PASSWORD_CONFIRM_MISMATCH);
        }

        String hospitalName = request.getClinicName().trim();
        String stateCode = tenantCodeGenerator.resolveStateCode(request.getState());
        String stateDisplay = tenantCodeGenerator.resolveStateDisplayName(request.getState());
        String baseTenantCode = tenantCodeGenerator.generate(hospitalName, request.getState());
        String tenantCode = tenantCodeGenerator.allocateUnique(baseTenantCode, this::isTenantCodeOrSchemaTaken);
        String schemaName = schemaProvisioningService.buildSchemaName(tenantCode);

        String contactEmail = request.getEmail().trim().toLowerCase();

        Tenant hospital = Tenant.builder()
                .tenantCode(tenantCode)
                .name(hospitalName)
                .clinicType(trimToNull(request.getClinicType()))
                .state(stateDisplay)
                .stateCode(stateCode)
                .city(trimToNull(request.getCity()))
                .pinCode(trimToNull(request.getPinCode()))
                .addressLine1(trimToNull(request.getAddressLine1()))
                .addressLine2(trimToNull(request.getAddressLine2()))
                .registrationNumberGst(trimToNull(request.getRegistrationNumberGst()))
                .logoUrl(trimToNull(request.getLogoUrl()))
                .fullName(request.getFullName().trim())
                .mobileNumber(trimToNull(request.getMobileNumber()))
                .email(contactEmail)
                .photoUrl(trimToNull(request.getPhotoUrl()))
                .schemaName(schemaName)
                .platform(false)
                .status(TenantStatus.PROVISIONING)
                .build();

        Tenant saved = tenantRepository.save(hospital);

        try {
            String provisionMessage = schemaProvisioningService.provisionSchema(schemaName);
            TenantRole hospitalAdminRole = tenantBootstrapService.ensureHospitalAdminRole(saved);
            saved.setStatus(TenantStatus.ACTIVE);
            saved.setProvisionMessage(provisionMessage);
            tenantRepository.save(saved);

            String loginEmail = contactEmail;

            if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(saved.getId(), loginEmail)) {
                throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + loginEmail);
            }

            AuthUser admin = AuthUser.builder()
                    .tenant(saved)
                    .email(loginEmail)
                    .fullName(request.getFullName().trim())
                    .mobileNumber(trimToNull(request.getMobileNumber()))
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(UserRole.ADMIN)
                    .tenantRole(hospitalAdminRole)
                    .status(UserStatus.ACTIVE)
                    .build();
            AuthUser savedAdmin = authUserRepository.save(admin);

            log.info("Onboarded hospital {} ({}) with admin {} schema {}",
                    tenantCode, hospitalName, loginEmail, schemaName);

            hospitalOnboardActivityLogger.recordOnboarded(
                    schemaName, hospitalName, tenantCode, currentPrincipal());

            HospitalOnboardResponse response = HospitalOnboardResponse.builder()
                    .hospital(authMapper.toTenantResponse(saved))
                    .admin(authMapper.toUserResponse(
                            savedAdmin, pageAccessService.resolvePageCodes(savedAdmin)))
                    .build();

            return ApiResponse.success(AuthMessages.HOSPITAL_ONBOARDED_SUCCESSFULLY, response);
        } catch (DuplicateResourceException | BadRequestException ex) {
            saved.setStatus(TenantStatus.FAILED);
            saved.setProvisionMessage(ex.getMessage());
            tenantRepository.save(saved);
            throw ex;
        } catch (Exception ex) {
            log.error("Hospital provision failed for {}: {}", tenantCode, ex.getMessage(), ex);
            saved.setStatus(TenantStatus.FAILED);
            saved.setProvisionMessage(ex.getMessage());
            tenantRepository.save(saved);
            throw new BadRequestException("Hospital schema provision failed: " + ex.getMessage());
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TenantResponse>> listHospitals() {
        requireSuperAdmin();
        List<TenantResponse> hospitals = tenantRepository
                .findAllByPlatformFalseAndDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(authMapper::toTenantResponse)
                .toList();
        return ApiResponse.success(AuthMessages.HOSPITALS_FETCHED_SUCCESSFULLY, hospitals);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TenantResponse> getHospital(UUID hospitalId) {
        requireSuperAdmin();
        return ApiResponse.success(authMapper.toTenantResponse(requireHospital(hospitalId)));
    }

    @Override
    public ApiResponse<TenantResponse> updateHospital(UUID hospitalId, UpdateHospitalRequest request) {
        requireSuperAdmin();

        Tenant hospital = requireHospital(hospitalId);

        if (StringUtils.hasText(request.getClinicName())) {
            hospital.setName(request.getClinicName().trim());
        }
        if (request.getClinicType() != null) {
            hospital.setClinicType(trimToNull(request.getClinicType()));
        }
        if (StringUtils.hasText(request.getState())) {
            String stateCode = tenantCodeGenerator.resolveStateCode(request.getState());
            String stateDisplay = tenantCodeGenerator.resolveStateDisplayName(request.getState());
            hospital.setState(stateDisplay);
            hospital.setStateCode(stateCode);
            // tenantCode / schemaName intentionally unchanged
        }
        if (request.getCity() != null) {
            hospital.setCity(trimToNull(request.getCity()));
        }
        if (request.getPinCode() != null) {
            hospital.setPinCode(trimToNull(request.getPinCode()));
        }
        if (request.getAddressLine1() != null) {
            hospital.setAddressLine1(trimToNull(request.getAddressLine1()));
        }
        if (request.getAddressLine2() != null) {
            hospital.setAddressLine2(trimToNull(request.getAddressLine2()));
        }
        if (request.getRegistrationNumberGst() != null) {
            hospital.setRegistrationNumberGst(trimToNull(request.getRegistrationNumberGst()));
        }
        if (request.getLogoUrl() != null) {
            hospital.setLogoUrl(trimToNull(request.getLogoUrl()));
        }
        if (StringUtils.hasText(request.getFullName())) {
            hospital.setFullName(request.getFullName().trim());
        }
        if (request.getMobileNumber() != null) {
            hospital.setMobileNumber(trimToNull(request.getMobileNumber()));
        }
        if (StringUtils.hasText(request.getEmail())) {
            hospital.setEmail(request.getEmail().trim().toLowerCase());
        }
        if (request.getPhotoUrl() != null) {
            hospital.setPhotoUrl(trimToNull(request.getPhotoUrl()));
        }

        Tenant saved = tenantRepository.save(hospital);
        log.info("Updated hospital profile for {}", saved.getTenantCode());

        return ApiResponse.success(AuthMessages.HOSPITAL_PROFILE_UPDATED, authMapper.toTenantResponse(saved));
    }

    @Override
    public ApiResponse<UserResponse> createHospitalAdmin(
            UUID hospitalId, CreateHospitalAdminRequest request) {
        requireSuperAdmin();

        Tenant hospital = requireHospital(hospitalId);
        if (hospital.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_AVAILABLE_FOR_REGISTRATION);
        }

        String email = request.getEmail().trim().toLowerCase();

        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(hospital.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + email);
        }

        TenantRole hospitalAdminRole = tenantBootstrapService.ensureHospitalAdminRole(hospital);

        AuthUser admin = AuthUser.builder()
                .tenant(hospital)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ADMIN)
                .tenantRole(hospitalAdminRole)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(admin);
        log.info("Created hospital ADMIN {} for tenant {}", email, hospital.getTenantCode());

        return ApiResponse.success(
                AuthMessages.HOSPITAL_ADMIN_CREATED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageAccessService.resolvePageCodes(saved)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserResponse>> listHospitalAdmins(UUID hospitalId) {
        requireSuperAdmin();
        Tenant hospital = requireHospital(hospitalId);

        List<UserResponse> admins = authUserRepository
                .findByTenantIdAndRoleAndDeletedFalseOrderByCreatedAtDesc(hospital.getId(), UserRole.ADMIN)
                .stream()
                .map(user -> authMapper.toUserResponse(user, pageAccessService.resolvePageCodes(user)))
                .toList();

        return ApiResponse.success(AuthMessages.HOSPITAL_ADMINS_FETCHED, admins);
    }

    @Override
    public ApiResponse<TenantResponse> updateHospitalStatus(
            UUID hospitalId, UpdateHospitalStatusRequest request) {
        requireSuperAdmin();

        Tenant hospital = requireHospital(hospitalId);
        TenantStatus status = request.getStatus();
        if (status == TenantStatus.PROVISIONING || status == TenantStatus.FAILED) {
            throw new BadRequestException("Cannot manually set status to " + status);
        }

        hospital.setStatus(status);
        tenantRepository.save(hospital);
        log.info("Updated hospital {} status to {}", hospital.getTenantCode(), status);

        return ApiResponse.success(
                AuthMessages.HOSPITAL_STATUS_UPDATED, authMapper.toTenantResponse(hospital));
    }

    @Override
    @Transactional(noRollbackFor = BadRequestException.class)
    public ApiResponse<TenantResponse> retryHospitalProvision(UUID hospitalId) {
        requireSuperAdmin();

        Tenant hospital = requireHospital(hospitalId);
        if (hospital.getStatus() != TenantStatus.FAILED) {
            throw new BadRequestException(AuthMessages.HOSPITAL_NOT_IN_FAILED_STATE);
        }

        hospital.setStatus(TenantStatus.PROVISIONING);
        hospital.setProvisionMessage("Retrying schema provision...");
        tenantRepository.save(hospital);

        try {
            String provisionMessage = schemaProvisioningService.provisionSchema(hospital.getSchemaName());
            tenantBootstrapService.ensureHospitalAdminRole(hospital);
            hospital.setStatus(TenantStatus.ACTIVE);
            hospital.setProvisionMessage(provisionMessage);
            tenantRepository.save(hospital);
            log.info("Retried hospital provision for {}", hospital.getTenantCode());

            hospitalOnboardActivityLogger.recordProvisionCompleted(
                    hospital.getSchemaName(),
                    hospital.getName(),
                    hospital.getTenantCode(),
                    currentPrincipal());
        } catch (Exception ex) {
            log.error("Hospital provision retry failed for {}: {}", hospital.getTenantCode(), ex.getMessage(), ex);
            hospital.setStatus(TenantStatus.FAILED);
            hospital.setProvisionMessage(ex.getMessage());
            tenantRepository.save(hospital);
            throw new BadRequestException("Hospital schema provision failed: " + ex.getMessage());
        }

        return ApiResponse.success(
                AuthMessages.HOSPITAL_PROVISION_RETRIED, authMapper.toTenantResponse(hospital));
    }

    private Tenant createPlatformTenant() {
        Tenant platform = Tenant.builder()
                .tenantCode(PLATFORM_TENANT_CODE)
                .name("Ayurvedaa Platform")
                .schemaName(PLATFORM_SCHEMA)
                .platform(true)
                .status(TenantStatus.ACTIVE)
                .provisionMessage("Platform control-plane tenant")
                .build();
        return tenantRepository.save(platform);
    }

    /** True when tenantCode or its derived schemaName is already used by a non-deleted tenant. */
    private boolean isTenantCodeOrSchemaTaken(String tenantCode) {
        if (tenantRepository.existsByTenantCodeIgnoreCaseAndDeletedFalse(tenantCode)) {
            return true;
        }
        String schemaName = schemaProvisioningService.buildSchemaName(tenantCode);
        return tenantRepository.existsBySchemaNameIgnoreCaseAndDeletedFalse(schemaName);
    }

    private Tenant requireHospital(UUID hospitalId) {
        Tenant tenant = tenantRepository.findByIdAndDeletedFalse(hospitalId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.HOSPITAL_NOT_FOUND));
        if (Boolean.TRUE.equals(tenant.getPlatform())) {
            throw new BadRequestException(AuthMessages.CANNOT_MODIFY_PLATFORM_AS_HOSPITAL);
        }
        return tenant;
    }

    private void requireSuperAdmin() {
        AuthPrincipal principal = currentPrincipal();
        if (!UserRole.SUPER_ADMIN.name().equals(principal.getRole())) {
            throw new ForbiddenException(AuthMessages.ONLY_SUPER_ADMIN_ALLOWED);
        }
    }

    private AuthPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new UnauthorizedException(AppConstants.AUTHENTICATION_REQUIRED);
        }
        return principal;
    }

}
