package com.ayurveda.auth.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.request.BootstrapSuperAdminRequest;
import com.ayurveda.auth.dto.request.CreateHospitalAdminRequest;
import com.ayurveda.auth.dto.request.OnboardHospitalRequest;
import com.ayurveda.auth.dto.request.UpdateHospitalStatusRequest;
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
import com.ayurveda.auth.service.PageAccessService;
import com.ayurveda.auth.service.PlatformService;
import com.ayurveda.auth.service.SchemaProvisioningService;
import com.ayurveda.auth.service.TenantBootstrapService;
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
    private final PageAccessService pageAccessService;
    private final PasswordEncoder passwordEncoder;
    private final AuthMapper authMapper;

    @Override
    public ApiResponse<UserResponse> bootstrapSuperAdmin(BootstrapSuperAdminRequest request) {
        if (authUserRepository.existsByRoleAndDeletedFalse(UserRole.SUPER_ADMIN)) {
            throw new BadRequestException(AuthMessages.SUPER_ADMIN_ALREADY_EXISTS);
        }

        Tenant platform = tenantRepository.findFirstByPlatformTrueAndDeletedFalse()
                .orElseGet(this::createPlatformTenant);

        String username = request.getUsername().trim().toLowerCase();
        String email = username;

        if (authUserRepository.existsByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(
                platform.getId(), username)) {
            throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
        }

        AuthUser superAdmin = AuthUser.builder()
                .tenant(platform)
                .username(username)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.SUPER_ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(superAdmin);
        log.info("Bootstrapped platform SUPER_ADMIN {}", username);

        return ApiResponse.success(
                AuthMessages.PLATFORM_BOOTSTRAPPED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageAccessService.resolvePageCodes(saved)));
    }

    @Override
    public ApiResponse<TenantResponse> onboardHospital(OnboardHospitalRequest request) {
        requireSuperAdmin();

        String tenantCode = request.getTenantCode().trim().toUpperCase();
        if (tenantRepository.existsByTenantCodeIgnoreCaseAndDeletedFalse(tenantCode)) {
            throw new DuplicateResourceException(AuthMessages.TENANT_CODE_ALREADY_EXISTS + tenantCode);
        }

        String schemaName = schemaProvisioningService.buildSchemaName(tenantCode);
        if (tenantRepository.existsBySchemaNameIgnoreCaseAndDeletedFalse(schemaName)) {
            throw new DuplicateResourceException(AuthMessages.SCHEMA_ALREADY_EXISTS + schemaName);
        }

        Tenant hospital = Tenant.builder()
                .tenantCode(tenantCode)
                .name(request.getName().trim())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .schemaName(schemaName)
                .platform(false)
                .status(TenantStatus.PROVISIONING)
                .build();

        Tenant saved = tenantRepository.save(hospital);

        try {
            schemaProvisioningService.createSchema(schemaName);
            tenantBootstrapService.ensureHospitalAdminRole(saved);
            saved.setStatus(TenantStatus.ACTIVE);
            saved.setProvisionMessage("Schema " + schemaName + " created. Hospital domain migrations are step-2.");
            tenantRepository.save(saved);
            log.info("Onboarded hospital {} with schema {}", tenantCode, schemaName);
        } catch (Exception ex) {
            log.error("Hospital provision failed for {}: {}", tenantCode, ex.getMessage(), ex);
            saved.setStatus(TenantStatus.FAILED);
            saved.setProvisionMessage(ex.getMessage());
            tenantRepository.save(saved);
            throw new BadRequestException("Hospital schema provision failed: " + ex.getMessage());
        }

        return ApiResponse.success(
                AuthMessages.HOSPITAL_ONBOARDED_SUCCESSFULLY, authMapper.toTenantResponse(saved));
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
    public ApiResponse<UserResponse> createHospitalAdmin(
            UUID hospitalId, CreateHospitalAdminRequest request) {
        requireSuperAdmin();

        Tenant hospital = requireHospital(hospitalId);
        if (hospital.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_AVAILABLE_FOR_REGISTRATION);
        }

        String username = request.getUsername().trim().toLowerCase();
        String email = username;

        if (authUserRepository.existsByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(
                hospital.getId(), username)) {
            throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
        }
        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(hospital.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + email);
        }

        TenantRole hospitalAdminRole = tenantBootstrapService.ensureHospitalAdminRole(hospital);

        AuthUser admin = AuthUser.builder()
                .tenant(hospital)
                .username(username)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.ADMIN)
                .tenantRole(hospitalAdminRole)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(admin);
        log.info("Created hospital ADMIN {} for tenant {}", username, hospital.getTenantCode());

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
            schemaProvisioningService.createSchema(hospital.getSchemaName());
            tenantBootstrapService.ensureHospitalAdminRole(hospital);
            hospital.setStatus(TenantStatus.ACTIVE);
            hospital.setProvisionMessage(
                    "Schema " + hospital.getSchemaName()
                            + " created. Hospital domain migrations are step-2.");
            tenantRepository.save(hospital);
            log.info("Retried hospital provision for {}", hospital.getTenantCode());
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
