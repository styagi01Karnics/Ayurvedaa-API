package com.ayurveda.auth.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.request.CreateTenantRoleRequest;
import com.ayurveda.auth.dto.request.UpdateTenantRoleRequest;
import com.ayurveda.auth.dto.response.TenantRoleResponse;
import com.ayurveda.auth.dto.response.UiPageResponse;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.entity.TenantRolePage;
import com.ayurveda.auth.entity.UiPage;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.AuthUserRepository;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.auth.repository.TenantRolePageRepository;
import com.ayurveda.auth.repository.TenantRoleRepository;
import com.ayurveda.auth.repository.UiPageRepository;
import com.ayurveda.auth.security.AuthPrincipal;
import com.ayurveda.auth.service.RoleService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityActionType;
import com.ayurveda.common.activity.ActivityLogPublisher;
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
public class RoleServiceImpl implements RoleService {

    private final TenantRepository tenantRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantRolePageRepository tenantRolePageRepository;
    private final UiPageRepository uiPageRepository;
    private final AuthUserRepository authUserRepository;
    private final AuthMapper authMapper;
    private final ActivityLogPublisher activityLogPublisher;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UiPageResponse>> listUiPages() {
        requireHospitalAdmin();
        List<UiPageResponse> pages = uiPageRepository.findAllByDeletedFalseOrderBySortOrderAsc()
                .stream()
                .map(authMapper::toUiPageResponse)
                .toList();
        return ApiResponse.success(AuthMessages.UI_PAGES_FETCHED_SUCCESSFULLY, pages);
    }

    @Override
    public ApiResponse<TenantRoleResponse> createRole(CreateTenantRoleRequest request) {
        // Hospital ADMIN only — platform Super Admin is not used for Role Management UI.
        AuthPrincipal principal = requireHospitalAdmin();
        Tenant tenant = requireHospitalTenant(principal.getTenantId());

        String roleCode = resolveRoleCode(request.getRoleCode(), request.getRoleName());
        if (tenantRoleRepository.existsByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(
                tenant.getId(), roleCode)) {
            throw new DuplicateResourceException(AuthMessages.ROLE_CODE_ALREADY_EXISTS + roleCode);
        }

        List<UiPage> pages = resolvePages(request.getPageCodes());
        boolean active = request.getActive() == null || Boolean.TRUE.equals(request.getActive());

        TenantRole role = TenantRole.builder()
                .tenant(tenant)
                .roleCode(roleCode)
                .roleName(request.getRoleName().trim())
                .description(request.getDescription())
                .systemRole(false)
                .active(active)
                .build();
        TenantRole saved = tenantRoleRepository.save(role);
        replaceRolePages(saved, pages);

        log.info("Hospital admin created role {} for hospital {}", roleCode, tenant.getTenantCode());

        activityLogPublisher.record(
                "Settings",
                ActivityActionType.CREATED,
                "Role " + saved.getRoleName(),
                null,
                null,
                principal.getUserId(),
                principal.getEmail(),
                principal.getRole());

        return ApiResponse.success(
                AuthMessages.ROLE_CREATED_SUCCESSFULLY,
                toResponse(saved, toPageCodes(pages)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<TenantRoleResponse>> listRoles() {
        AuthPrincipal principal = requireHospitalAdmin();
        List<TenantRole> roles = tenantRoleRepository
                .findAllByTenantIdAndDeletedFalseOrderByRoleNameAsc(principal.getTenantId());

        List<TenantRoleResponse> response = roles.stream()
                .map(role -> toResponse(role, loadPageCodes(role.getId())))
                .toList();

        return ApiResponse.success(AuthMessages.ROLES_FETCHED_SUCCESSFULLY, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TenantRoleResponse> getRole(UUID roleId) {
        AuthPrincipal principal = requireHospitalAdmin();
        TenantRole role = tenantRoleRepository
                .findByIdAndTenantIdAndDeletedFalse(roleId, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.ROLE_NOT_FOUND));
        return ApiResponse.success(toResponse(role, loadPageCodes(role.getId())));
    }

    @Override
    public ApiResponse<TenantRoleResponse> updateRole(UUID roleId, UpdateTenantRoleRequest request) {
        AuthPrincipal principal = requireHospitalAdmin();
        TenantRole role = tenantRoleRepository
                .findByIdAndTenantIdAndDeletedFalse(roleId, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.ROLE_NOT_FOUND));

        List<UiPage> pages = resolvePages(request.getPageCodes());
        role.setRoleName(request.getRoleName().trim());
        role.setDescription(request.getDescription());
        if (request.getActive() != null) {
            role.setActive(request.getActive());
        }
        tenantRoleRepository.save(role);
        replaceRolePages(role, pages);

        log.info("Hospital admin updated role {} for hospital {}", role.getRoleCode(), principal.getTenantCode());
        return ApiResponse.success(
                AuthMessages.ROLE_UPDATED_SUCCESSFULLY,
                toResponse(role, toPageCodes(pages)));
    }

    @Override
    public ApiResponse<Void> deleteRole(UUID roleId) {
        AuthPrincipal principal = requireHospitalAdmin();
        TenantRole role = tenantRoleRepository
                .findByIdAndTenantIdAndDeletedFalse(roleId, principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.ROLE_NOT_FOUND));

        if (Boolean.TRUE.equals(role.getSystemRole())) {
            throw new BadRequestException(AuthMessages.SYSTEM_ROLE_CANNOT_BE_DELETED);
        }

        softDeleteRolePages(role.getId());
        role.setDeleted(true);
        tenantRoleRepository.save(role);

        log.info("Hospital admin deleted role {} for hospital {}", role.getRoleCode(), principal.getTenantCode());
        return ApiResponse.success(AuthMessages.ROLE_DELETED_SUCCESSFULLY, null);
    }

    private TenantRoleResponse toResponse(TenantRole role, List<String> pageCodes) {
        long userCount = authUserRepository.countByTenantRoleIdAndDeletedFalse(role.getId());
        return authMapper.toTenantRoleResponse(role, pageCodes, userCount);
    }

    private String resolveRoleCode(String roleCode, String roleName) {
        if (StringUtils.hasText(roleCode)) {
            return roleCode.trim().toUpperCase(Locale.ROOT);
        }
        String generated = roleName.trim().toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        if (!StringUtils.hasText(generated)) {
            throw new BadRequestException("Role name is invalid for generating role code.");
        }
        if (generated.length() > 50) {
            generated = generated.substring(0, 50);
        }
        return generated;
    }

    private List<UiPage> resolvePages(List<String> pageCodes) {
        if (pageCodes == null || pageCodes.isEmpty()) {
            throw new BadRequestException(AuthMessages.UI_PAGES_REQUIRED);
        }
        List<String> normalized = pageCodes.stream()
                .map(code -> code.trim().toUpperCase(Locale.ROOT))
                .distinct()
                .toList();

        List<UiPage> pages = uiPageRepository.findAllByPageCodeInAndDeletedFalse(normalized);
        Set<String> found = pages.stream()
                .map(UiPage::getPageCode)
                .map(code -> code.toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        List<String> missing = normalized.stream().filter(code -> !found.contains(code)).toList();
        if (!missing.isEmpty()) {
            throw new BadRequestException(AuthMessages.INVALID_UI_PAGE_CODES + missing);
        }
        return pages;
    }

    private void replaceRolePages(TenantRole role, List<UiPage> pages) {
        softDeleteRolePages(role.getId());
        for (UiPage page : pages) {
            tenantRolePageRepository.save(TenantRolePage.builder()
                    .tenantRole(role)
                    .uiPage(page)
                    .build());
        }
    }

    private void softDeleteRolePages(UUID roleId) {
        List<TenantRolePage> existing =
                tenantRolePageRepository.findAllByTenantRoleIdAndDeletedFalse(roleId);
        for (TenantRolePage mapping : existing) {
            mapping.setDeleted(true);
        }
        tenantRolePageRepository.saveAll(existing);
    }

    private List<String> loadPageCodes(UUID roleId) {
        return tenantRolePageRepository.findAllByTenantRoleIdAndDeletedFalse(roleId).stream()
                .map(TenantRolePage::getUiPage)
                .filter(page -> page != null && !Boolean.TRUE.equals(page.getDeleted()))
                .map(UiPage::getPageCode)
                .toList();
    }

    private List<String> toPageCodes(List<UiPage> pages) {
        return pages.stream().map(UiPage::getPageCode).collect(Collectors.toCollection(ArrayList::new));
    }

    private Tenant requireHospitalTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findByIdAndDeletedFalse(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_NOT_FOUND));
        if (Boolean.TRUE.equals(tenant.getPlatform())) {
            throw new BadRequestException(AuthMessages.CANNOT_ASSIGN_ROLE_ON_PLATFORM_TENANT);
        }
        return tenant;
    }

    private AuthPrincipal requireHospitalAdmin() {
        AuthPrincipal principal = currentPrincipal();
        if (!UserRole.ADMIN.name().equals(principal.getRole())) {
            throw new ForbiddenException(
                    "Only hospital admin can manage roles. Platform Super Admin is not used for Role Management.");
        }
        return principal;
    }

    private AuthPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new UnauthorizedException(AppConstants.AUTHENTICATION_REQUIRED);
        }
        return principal;
    }

}
