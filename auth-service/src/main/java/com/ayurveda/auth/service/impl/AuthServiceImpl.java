package com.ayurveda.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.dto.request.ChangePasswordRequest;
import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.UpdateProfileRequest;
import com.ayurveda.auth.dto.request.UpdateUserRequest;
import com.ayurveda.auth.dto.request.UpdateUserStatusRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
import com.ayurveda.auth.dto.response.PagedResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TokenValidationResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.PasswordResetToken;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.AuthUserRepository;
import com.ayurveda.auth.repository.PasswordResetTokenRepository;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.auth.repository.TenantRoleRepository;
import com.ayurveda.auth.security.AuthPrincipal;
import com.ayurveda.auth.security.JwtService;
import com.ayurveda.auth.security.TenantContext;
import com.ayurveda.auth.service.AuthService;
import com.ayurveda.auth.service.PageAccessService;
import com.ayurveda.auth.service.TenantBootstrapService;
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
public class AuthServiceImpl implements AuthService {

    private static final int RESET_TOKEN_EXPIRY_MINUTES = 30;

    private final TenantRepository tenantRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final PageAccessService pageAccessService;
    private final TenantBootstrapService tenantBootstrapService;
    private final ActivityLogPublisher activityLogPublisher;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AuthTokenResponse> login(LoginRequest request) {
        String loginId = request.getUsernameOrEmail().trim();
        boolean hasTenant = request.getTenantCode() != null && !request.getTenantCode().isBlank();

        AuthUser user;
        Tenant tenant;

        if (hasTenant) {
            tenant = resolveHospitalTenant(request.getTenantCode());
            user = resolveUserForTenant(tenant.getId(), loginId)
                    .orElseThrow(() -> new UnauthorizedException(AuthMessages.USER_NOT_FOUND_FOR_TENANT));

            if (user.getRole() == UserRole.SUPER_ADMIN) {
                throw new BadRequestException(AuthMessages.TENANT_NOT_ALLOWED_FOR_SUPER_ADMIN_LOGIN);
            }
        } else {
            user = resolveSuperAdminByUsernameOrEmail(loginId)
                    .orElseThrow(() -> new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS));

            if (user.getRole() != UserRole.SUPER_ADMIN) {
                throw new BadRequestException(AuthMessages.TENANT_REQUIRED_FOR_HOSPITAL_LOGIN);
            }

            tenant = user.getTenant();
            if (!Boolean.TRUE.equals(tenant.getPlatform())) {
                throw new ForbiddenException(AuthMessages.SUPER_ADMIN_LOGIN_ONLY_WITHOUT_TENANT);
            }
        }

        if (Boolean.TRUE.equals(tenant.getDeleted()) || tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_ACTIVE);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.USER_ACCOUNT_NOT_ACTIVE_WITH_STATUS + user.getStatus());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS);
        }

        List<String> pageCodes = pageAccessService.resolvePageCodes(user);
        String token = jwtService.generateToken(user, pageCodes);

        AuthTokenResponse response = AuthTokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .user(authMapper.toUserResponse(user, pageCodes))
                .tenant(authMapper.toTenantResponse(tenant))
                .build();

        log.info("User {} logged in for tenant {}", user.getUsername(), tenant.getTenantCode());

        return ApiResponse.success(AuthMessages.LOGIN_SUCCESSFUL, response);
    }

    @Override
    public ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        String loginId = request.getUsernameOrEmail().trim();
        boolean hasTenant = request.getTenantCode() != null && !request.getTenantCode().isBlank();

        Optional<AuthUser> userOpt;
        if (hasTenant) {
            Tenant tenant = resolveHospitalTenant(request.getTenantCode());
            userOpt = resolveUserForTenant(tenant.getId(), loginId)
                    .filter(u -> u.getRole() != UserRole.SUPER_ADMIN);
        } else {
            userOpt = resolveSuperAdminByUsernameOrEmail(loginId);
        }

        if (userOpt.isEmpty()) {
            return ApiResponse.success(
                    AuthMessages.PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS,
                    ForgotPasswordResponse.builder()
                            .message(AuthMessages.PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS)
                            .build());
        }

        AuthUser user = userOpt.get();
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId());

        String rawToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESET_TOKEN_EXPIRY_MINUTES);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hashToken(rawToken))
                .expiresAt(expiresAt)
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset token generated for user {}", user.getUsername());

        return ApiResponse.success(
                AuthMessages.PASSWORD_RESET_TOKEN_GENERATED,
                ForgotPasswordResponse.builder()
                        .message(AuthMessages.PASSWORD_RESET_TOKEN_GENERATED)
                        .resetToken(rawToken)
                        .expiresAt(expiresAt)
                        .build());
    }

    @Override
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(AuthMessages.NEW_PASSWORD_CONFIRM_MISMATCH);
        }

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByTokenHashAndUsedFalseAndDeletedFalse(hashToken(request.getToken()))
                .orElseThrow(() -> new BadRequestException(AuthMessages.INVALID_OR_EXPIRED_RESET_TOKEN));

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(AuthMessages.INVALID_OR_EXPIRED_RESET_TOKEN);
        }

        AuthUser user = resetToken.getUser();
        if (Boolean.TRUE.equals(user.getDeleted()) || user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.USER_ACCOUNT_NOT_ACTIVE);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        authUserRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId());

        log.info("Password reset completed for user {}", user.getUsername());

        return ApiResponse.success(AuthMessages.PASSWORD_RESET_SUCCESSFUL, null);
    }

    @Override
    public ApiResponse<UserResponse> registerUser(RegisterUserRequest request) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        Tenant tenant = tenantRepository.findById(principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_NOT_FOUND));

        if (Boolean.TRUE.equals(tenant.getDeleted()) || tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_AVAILABLE_FOR_REGISTRATION);
        }
        if (Boolean.TRUE.equals(tenant.getPlatform())) {
            throw new BadRequestException(AuthMessages.CANNOT_ASSIGN_ROLE_ON_PLATFORM_TENANT);
        }

        String email = request.getUsername().trim().toLowerCase();
        String username = email;

        if (authUserRepository.existsByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(tenant.getId(), username)) {
            throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
        }

        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(tenant.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + email);
        }

        if (request.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException(AuthMessages.CANNOT_CREATE_SUPER_ADMIN_VIA_TENANT_REGISTRATION);
        }

        TenantRole tenantRole = resolveTenantRoleForUser(
                tenant.getId(), request.getRole(), request.getTenantRoleId());

        AuthUser user = AuthUser.builder()
                .tenant(tenant)
                .username(username)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .tenantRole(tenantRole)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(user);
        List<String> pageCodes = pageAccessService.resolvePageCodes(saved);

        log.info("Registered user {} with role {} for tenant {}", username, request.getRole(), tenant.getTenantCode());

        activityLogPublisher.record(
                "Settings",
                ActivityActionType.CREATED,
                "User " + username,
                null,
                request.getRole().name(),
                principal.getUserId(),
                principal.getEmail(),
                principal.getRole());

        return ApiResponse.success(
                AuthMessages.USER_REGISTERED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageCodes));
    }

    @Override
    public ApiResponse<UserResponse> updateUser(UUID userId, UpdateUserRequest request) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        AuthUser user = requireTenantUser(userId, principal.getTenantId());
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException(AuthMessages.CANNOT_MODIFY_SUPER_ADMIN);
        }

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }

        if (StringUtils.hasText(request.getUsername())) {
            String username = request.getUsername().trim().toLowerCase();
            if (!username.equalsIgnoreCase(user.getUsername())
                    && authUserRepository.existsByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(
                            principal.getTenantId(), username)) {
                throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
            }
            if (!username.equalsIgnoreCase(user.getEmail())
                    && authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(
                            principal.getTenantId(), username)) {
                throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + username);
            }
            user.setUsername(username);
            user.setEmail(username);
        }

        if (request.getRole() != null) {
            if (request.getRole() == UserRole.SUPER_ADMIN) {
                throw new BadRequestException(AuthMessages.CANNOT_CREATE_SUPER_ADMIN_VIA_TENANT_REGISTRATION);
            }
            user.setRole(request.getRole());
        }

        if (request.getTenantRoleId() != null || request.getRole() != null) {
            UserRole effectiveRole = user.getRole();
            TenantRole tenantRole = resolveTenantRoleForUser(
                    principal.getTenantId(), effectiveRole, request.getTenantRoleId());
            user.setTenantRole(tenantRole);
        }

        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        AuthUser saved = authUserRepository.save(user);
        List<String> pageCodes = pageAccessService.resolvePageCodes(saved);

        activityLogPublisher.record(
                "Settings",
                ActivityActionType.UPDATED,
                "User " + saved.getUsername(),
                null,
                saved.getRole().name(),
                principal.getUserId(),
                principal.getEmail(),
                principal.getRole());

        return ApiResponse.success(
                AuthMessages.USER_UPDATED_SUCCESSFULLY, authMapper.toUserResponse(saved, pageCodes));
    }

    @Override
    public ApiResponse<UserResponse> updateUserStatus(UUID userId, UpdateUserStatusRequest request) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        AuthUser user = requireTenantUser(userId, principal.getTenantId());
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException(AuthMessages.CANNOT_MODIFY_SUPER_ADMIN);
        }

        user.setStatus(request.getStatus());
        AuthUser saved = authUserRepository.save(user);

        activityLogPublisher.record(
                "Settings",
                ActivityActionType.UPDATED,
                "User status " + saved.getUsername(),
                null,
                request.getStatus().name(),
                principal.getUserId(),
                principal.getEmail(),
                principal.getRole());

        return ApiResponse.success(
                AuthMessages.USER_STATUS_UPDATED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageAccessService.resolvePageCodes(saved)));
    }

    @Override
    public ApiResponse<Void> deleteUser(UUID userId) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        if (principal.getUserId().equals(userId)) {
            throw new BadRequestException(AuthMessages.CANNOT_DELETE_SELF);
        }

        AuthUser user = requireTenantUser(userId, principal.getTenantId());
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException(AuthMessages.CANNOT_MODIFY_SUPER_ADMIN);
        }

        user.setDeleted(true);
        user.setStatus(UserStatus.INACTIVE);
        authUserRepository.save(user);
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId());

        activityLogPublisher.record(
                "Settings",
                ActivityActionType.DELETED,
                "User " + user.getUsername(),
                null,
                user.getRole().name(),
                principal.getUserId(),
                principal.getEmail(),
                principal.getRole());

        return ApiResponse.success(AuthMessages.USER_DELETED_SUCCESSFULLY, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserResponse> getUserById(UUID userId) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        AuthUser user = requireTenantUser(userId, principal.getTenantId());
        return ApiResponse.success(
                authMapper.toUserResponse(user, pageAccessService.resolvePageCodes(user)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserResponse> getCurrentUser() {
        AuthPrincipal principal = currentPrincipal();
        AuthUser user = authUserRepository.findByIdAndDeletedFalse(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));

        return ApiResponse.success(
                authMapper.toUserResponse(user, pageAccessService.resolvePageCodes(user)));
    }

    @Override
    public ApiResponse<UserResponse> updateProfile(UpdateProfileRequest request) {
        AuthPrincipal principal = currentPrincipal();
        AuthUser user = authUserRepository.findByIdAndDeletedFalse(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));

        if (StringUtils.hasText(request.getFullName())) {
            user.setFullName(request.getFullName().trim());
        }

        AuthUser saved = authUserRepository.save(user);
        return ApiResponse.success(
                AuthMessages.PROFILE_UPDATED_SUCCESSFULLY,
                authMapper.toUserResponse(saved, pageAccessService.resolvePageCodes(saved)));
    }

    @Override
    public ApiResponse<Void> changePassword(ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(AuthMessages.NEW_PASSWORD_CONFIRM_MISMATCH);
        }

        AuthPrincipal principal = currentPrincipal();
        AuthUser user = authUserRepository.findByIdAndDeletedFalse(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException(AuthMessages.CURRENT_PASSWORD_INCORRECT);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        authUserRepository.save(user);
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId());

        log.info("Password changed for user {}", user.getUsername());
        return ApiResponse.success(AuthMessages.PASSWORD_CHANGED_SUCCESSFULLY, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserResponse>> getUsersForCurrentTenant() {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        List<UserResponse> users = authUserRepository
                .findByTenantIdAndDeletedFalse(principal.getTenantId())
                .stream()
                .map(user -> authMapper.toUserResponse(user, pageAccessService.resolvePageCodes(user)))
                .toList();

        log.info("Fetched {} users for tenant {}", users.size(), principal.getTenantCode());

        return ApiResponse.success(users);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<PagedResponse<UserResponse>> getUsersForCurrentTenantPaged(int page, int size) {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 20 : Math.min(size, 100);

        Page<AuthUser> result = authUserRepository.findByTenantIdAndDeletedFalse(
                principal.getTenantId(),
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<UserResponse> content = result.getContent().stream()
                .map(user -> authMapper.toUserResponse(user, pageAccessService.resolvePageCodes(user)))
                .toList();

        PagedResponse<UserResponse> paged = PagedResponse.<UserResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();

        return ApiResponse.success(paged);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TenantResponse> getCurrentTenant() {
        AuthPrincipal principal = currentPrincipal();
        Tenant tenant = tenantRepository.findById(principal.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_NOT_FOUND));

        if (Boolean.TRUE.equals(tenant.getDeleted())) {
            throw new ResourceNotFoundException(AuthMessages.TENANT_NOT_FOUND);
        }

        return ApiResponse.success(authMapper.toTenantResponse(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TokenValidationResponse> validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ApiResponse.success(TokenValidationResponse.builder().valid(false).build());
        }

        try {
            AuthPrincipal principal = jwtService.parseToken(authorizationHeader.substring(7));
            TokenValidationResponse response = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(principal.getUserId())
                    .tenantId(principal.getTenantId())
                    .tenantCode(principal.getTenantCode())
                    .schemaName(principal.getSchemaName())
                    .email(principal.getEmail())
                    .role(principal.getRole())
                    .tenantRoleId(principal.getTenantRoleId())
                    .pageCodes(principal.getPageCodes())
                    .build();
            return ApiResponse.success(response);
        } catch (Exception ex) {
            log.info("Token validation failed: {}", ex.getMessage());
            return ApiResponse.success(TokenValidationResponse.builder().valid(false).build());
        }
    }

    private Optional<AuthUser> resolveSuperAdminByUsernameOrEmail(String usernameOrEmail) {
        String loginId = usernameOrEmail.toLowerCase();
        if (loginId.contains("@")) {
            return authUserRepository.findByRoleAndEmailIgnoreCaseAndDeletedFalse(
                    UserRole.SUPER_ADMIN, loginId);
        }
        return authUserRepository
                .findByRoleAndUsernameIgnoreCaseAndDeletedFalse(UserRole.SUPER_ADMIN, loginId)
                .or(() -> authUserRepository.findByRoleAndEmailIgnoreCaseAndDeletedFalse(
                        UserRole.SUPER_ADMIN, loginId));
    }

    private Optional<AuthUser> resolveUserForTenant(UUID tenantId, String usernameOrEmail) {
        String loginId = usernameOrEmail.toLowerCase();
        if (loginId.contains("@")) {
            return authUserRepository.findByTenantIdAndEmailIgnoreCaseAndDeletedFalse(tenantId, loginId);
        }
        return authUserRepository
                .findByTenantIdAndUsernameIgnoreCaseAndDeletedFalse(tenantId, loginId)
                .or(() -> authUserRepository.findByTenantIdAndEmailIgnoreCaseAndDeletedFalse(
                        tenantId, loginId));
    }

    private AuthUser requireTenantUser(UUID userId, UUID tenantId) {
        return authUserRepository.findByIdAndTenantIdAndDeletedFalse(userId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));
    }

    private TenantRole resolveTenantRoleForUser(UUID tenantId, UserRole role, UUID tenantRoleId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_NOT_FOUND));

        if (role == UserRole.ADMIN) {
            if (tenantRoleId != null) {
                return tenantRoleRepository
                        .findByIdAndTenantIdAndDeletedFalse(tenantRoleId, tenantId)
                        .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_ROLE_NOT_FOUND));
            }
            return tenantRoleRepository
                    .findByTenantIdAndRoleCodeIgnoreCaseAndDeletedFalse(
                            tenantId, TenantBootstrapService.HOSPITAL_ADMIN_ROLE_CODE)
                    .orElseGet(() -> tenantBootstrapService.ensureHospitalAdminRole(tenant));
        }

        if (tenantRoleId == null) {
            throw new BadRequestException(AuthMessages.TENANT_ROLE_REQUIRED_FOR_NON_ADMIN);
        }
        return tenantRoleRepository
                .findByIdAndTenantIdAndDeletedFalse(tenantRoleId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.TENANT_ROLE_NOT_FOUND));
    }

    private Tenant resolveHospitalTenant(String tenantCode) {
        if (tenantCode == null || tenantCode.isBlank()) {
            throw new BadRequestException(AuthMessages.TENANT_REQUIRED_FOR_HOSPITAL_LOGIN);
        }
        String code = tenantCode.trim().toUpperCase();
        Tenant tenant = tenantRepository.findByTenantCodeIgnoreCaseAndDeletedFalse(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AuthMessages.TENANT_NOT_FOUND_WITH_CODE + code));
        if (Boolean.TRUE.equals(tenant.getPlatform())) {
            throw new BadRequestException(AuthMessages.HOSPITAL_LOGIN_CANNOT_USE_PLATFORM_TENANT);
        }
        return tenant;
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not available", ex);
        }
    }

    private AuthPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new UnauthorizedException(AppConstants.AUTHENTICATION_REQUIRED);
        }
        if (TenantContext.getTenantId() == null) {
            TenantContext.set(
                    principal.getTenantId(), principal.getTenantCode(), principal.getSchemaName());
        }
        return principal;
    }

    private void requireTenantAdmin(AuthPrincipal principal) {
        if (!UserRole.ADMIN.name().equals(principal.getRole())
                && !UserRole.SUPER_ADMIN.name().equals(principal.getRole())) {
            throw new ForbiddenException(AuthMessages.ONLY_ADMIN_ALLOWED);
        }
    }

}
