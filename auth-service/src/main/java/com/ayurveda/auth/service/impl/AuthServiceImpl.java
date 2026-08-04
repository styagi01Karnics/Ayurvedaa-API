package com.ayurveda.auth.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.auth.dto.request.ForgotPasswordRequest;
import com.ayurveda.auth.dto.request.LoginRequest;
import com.ayurveda.auth.dto.request.RegisterTenantRequest;
import com.ayurveda.auth.dto.request.RegisterUserRequest;
import com.ayurveda.auth.dto.request.ResetPasswordRequest;
import com.ayurveda.auth.dto.request.SignUpRequest;
import com.ayurveda.auth.dto.response.AuthTokenResponse;
import com.ayurveda.auth.dto.response.ForgotPasswordResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TokenValidationResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.PasswordResetToken;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;
import com.ayurveda.auth.constant.AuthMessages;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.auth.repository.AuthUserRepository;
import com.ayurveda.auth.repository.PasswordResetTokenRepository;
import com.ayurveda.auth.repository.TenantRepository;
import com.ayurveda.auth.security.AuthPrincipal;
import com.ayurveda.auth.security.JwtService;
import com.ayurveda.auth.security.TenantContext;
import com.ayurveda.auth.service.AuthService;
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
public class AuthServiceImpl implements AuthService {

    private static final int RESET_TOKEN_EXPIRY_MINUTES = 30;

    private final TenantRepository tenantRepository;
    private final AuthUserRepository authUserRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Override
    public ApiResponse<TenantResponse> registerTenant(RegisterTenantRequest request) {
        String tenantCode = request.getTenantCode().trim().toUpperCase();

        if (tenantRepository.existsByTenantCodeIgnoreCaseAndDeletedFalse(tenantCode)) {
            throw new DuplicateResourceException(AuthMessages.TENANT_CODE_ALREADY_EXISTS + tenantCode);
        }

        if (authUserRepository.existsByUsernameIgnoreCaseAndDeletedFalse(request.getAdminUsername().trim())) {
            throw new DuplicateResourceException(
                    AuthMessages.USERNAME_ALREADY_EXISTS + request.getAdminUsername());
        }

        Tenant tenant = Tenant.builder()
                .tenantCode(tenantCode)
                .name(request.getName().trim())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(TenantStatus.ACTIVE)
                .build();

        Tenant savedTenant = tenantRepository.save(tenant);

        AuthUser admin = AuthUser.builder()
                .tenant(savedTenant)
                .username(request.getAdminUsername().trim().toLowerCase())
                .email(request.getAdminEmail().trim().toLowerCase())
                .fullName(request.getAdminFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getAdminPassword()))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        authUserRepository.save(admin);

        log.info("Registered tenant {} with admin {}", tenantCode, admin.getEmail());

        return ApiResponse.success(
                AuthMessages.TENANT_REGISTERED_SUCCESSFULLY, authMapper.toTenantResponse(savedTenant));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<AuthTokenResponse> login(LoginRequest request) {
        String loginId = request.getUsernameOrEmail().trim();

        AuthUser user = resolveUserByUsernameOrEmail(loginId)
                .orElseThrow(() -> new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS));

        Tenant tenant = user.getTenant();

        if (Boolean.TRUE.equals(tenant.getDeleted()) || tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_ACTIVE);
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.USER_ACCOUNT_NOT_ACTIVE_WITH_STATUS + user.getStatus());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException(AuthMessages.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user);

        AuthTokenResponse response = AuthTokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .user(authMapper.toUserResponse(user))
                .tenant(authMapper.toTenantResponse(tenant))
                .build();

        log.info("User {} logged in for tenant {}", user.getUsername(), tenant.getTenantCode());

        return ApiResponse.success(AuthMessages.LOGIN_SUCCESSFUL, response);
    }

    @Override
    public ApiResponse<AuthTokenResponse> signUp(SignUpRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(AuthMessages.PASSWORD_CONFIRM_MISMATCH);
        }

        String tenantCode = request.getTenantCode().trim().toUpperCase();
        Tenant tenant = tenantRepository.findByTenantCodeIgnoreCaseAndDeletedFalse(tenantCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        AuthMessages.TENANT_NOT_FOUND_WITH_CODE + tenantCode));

        if (tenant.getStatus() != TenantStatus.ACTIVE) {
            throw new ForbiddenException(AuthMessages.TENANT_NOT_ACTIVE);
        }

        String username = request.getUsername().trim().toLowerCase();
        String email = request.getEmail().trim().toLowerCase();

        if (authUserRepository.existsByUsernameIgnoreCaseAndDeletedFalse(username)) {
            throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
        }

        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(tenant.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.EMAIL_ALREADY_REGISTERED_FOR_TENANT);
        }

        AuthUser user = AuthUser.builder()
                .tenant(tenant)
                .username(username)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.RECEPTIONIST)
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(user);
        String token = jwtService.generateToken(saved);

        AuthTokenResponse response = AuthTokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresInMs(jwtService.getExpirationMs())
                .user(authMapper.toUserResponse(saved))
                .tenant(authMapper.toTenantResponse(tenant))
                .build();

        log.info("User signed up: {} under tenant {}", username, tenantCode);

        return ApiResponse.success(AuthMessages.SIGN_UP_SUCCESSFUL, response);
    }

    @Override
    public ApiResponse<ForgotPasswordResponse> forgotPassword(ForgotPasswordRequest request) {
        String loginId = request.getUsernameOrEmail().trim();

        Optional<AuthUser> userOpt = resolveUserByUsernameOrEmail(loginId);
        if (userOpt.isEmpty()) {
            // Do not reveal whether the account exists
            return ApiResponse.success(
                    AuthMessages.PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS,
                    ForgotPasswordResponse.builder()
                            .message(AuthMessages.PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS)
                            .build());
        }

        AuthUser user = userOpt.get();
        passwordResetTokenRepository.invalidateActiveTokensForUser(user.getId());

        String rawToken = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
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

        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim().toLowerCase();

        if (authUserRepository.existsByUsernameIgnoreCaseAndDeletedFalse(username)) {
            throw new DuplicateResourceException(AuthMessages.USERNAME_ALREADY_EXISTS + username);
        }

        if (authUserRepository.existsByTenantIdAndEmailIgnoreCaseAndDeletedFalse(tenant.getId(), email)) {
            throw new DuplicateResourceException(AuthMessages.USER_EXISTS_FOR_TENANT_EMAIL + email);
        }

        if (request.getRole() == UserRole.SUPER_ADMIN) {
            throw new BadRequestException(AuthMessages.CANNOT_CREATE_SUPER_ADMIN_VIA_TENANT_REGISTRATION);
        }

        AuthUser user = AuthUser.builder()
                .tenant(tenant)
                .username(username)
                .email(email)
                .fullName(request.getFullName().trim())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        AuthUser saved = authUserRepository.save(user);

        log.info("Registered user {} with role {} for tenant {}", username, request.getRole(), tenant.getTenantCode());

        return ApiResponse.success(AuthMessages.USER_REGISTERED_SUCCESSFULLY, authMapper.toUserResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<UserResponse> getCurrentUser() {
        AuthPrincipal principal = currentPrincipal();
        AuthUser user = authUserRepository.findByIdAndDeletedFalse(principal.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(AuthMessages.USER_NOT_FOUND));

        log.info("Fetched current user {}", user.getUsername());

        return ApiResponse.success(authMapper.toUserResponse(user));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<UserResponse>> getUsersForCurrentTenant() {
        AuthPrincipal principal = currentPrincipal();
        requireTenantAdmin(principal);

        List<UserResponse> users = authUserRepository
                .findByTenantIdAndDeletedFalse(principal.getTenantId())
                .stream()
                .map(authMapper::toUserResponse)
                .toList();

        log.info("Fetched {} users for tenant {}", users.size(), principal.getTenantCode());

        return ApiResponse.success(users);
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

        log.info("Fetched current tenant {}", tenant.getTenantCode());

        return ApiResponse.success(authMapper.toTenantResponse(tenant));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<TokenValidationResponse> validateToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("Token validation failed: missing or invalid Authorization header");
            return ApiResponse.success(TokenValidationResponse.builder().valid(false).build());
        }

        try {
            AuthPrincipal principal = jwtService.parseToken(authorizationHeader.substring(7));
            TokenValidationResponse response = TokenValidationResponse.builder()
                    .valid(true)
                    .userId(principal.getUserId())
                    .tenantId(principal.getTenantId())
                    .tenantCode(principal.getTenantCode())
                    .email(principal.getEmail())
                    .role(principal.getRole())
                    .build();
            log.info("Token validated for user {} tenant {}", principal.getEmail(), principal.getTenantCode());
            return ApiResponse.success(response);
        } catch (Exception ex) {
            log.info("Token validation failed: {}", ex.getMessage());
            return ApiResponse.success(TokenValidationResponse.builder().valid(false).build());
        }
    }

    private Optional<AuthUser> resolveUserByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return authUserRepository.findByEmailIgnoreCaseAndDeletedFalse(usernameOrEmail.toLowerCase());
        }
        return authUserRepository.findByUsernameIgnoreCaseAndDeletedFalse(usernameOrEmail.toLowerCase())
                .or(() -> authUserRepository.findByEmailIgnoreCaseAndDeletedFalse(usernameOrEmail.toLowerCase()));
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
            TenantContext.set(principal.getTenantId(), principal.getTenantCode());
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
