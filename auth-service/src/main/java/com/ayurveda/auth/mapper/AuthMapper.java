package com.ayurveda.auth.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ayurveda.auth.dto.response.HospitalMailResponse;
import com.ayurveda.auth.dto.response.PublicTenantLocationResponse;
import com.ayurveda.auth.dto.response.TenantPaymentGatewayResponse;
import com.ayurveda.auth.dto.response.TenantResponse;
import com.ayurveda.auth.dto.response.TenantRoleResponse;
import com.ayurveda.auth.dto.response.UiPageResponse;
import com.ayurveda.auth.dto.response.UserResponse;
import com.ayurveda.auth.entity.AuthUser;
import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantMailSettings;
import com.ayurveda.auth.entity.TenantPaymentGateway;
import com.ayurveda.auth.entity.TenantRole;
import com.ayurveda.auth.entity.UiPage;
import com.ayurveda.auth.enums.PaymentGatewayMode;
import com.ayurveda.auth.util.SecretMasking;
import com.ayurveda.common.crypto.SecretEncryption;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthMapper {

    private final SecretEncryption secretEncryption;

    public TenantResponse toTenantResponse(Tenant tenant) {
        return toTenantResponse(tenant, null);
    }

    public PublicTenantLocationResponse toPublicTenantLocationResponse(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        return PublicTenantLocationResponse.builder()
                .tenantCode(tenant.getTenantCode())
                .state(tenant.getState())
                .city(tenant.getCity())
                .build();
    }

    public TenantResponse toTenantResponse(Tenant tenant, TenantMailSettings mail) {
        if (tenant == null) {
            return null;
        }
        return TenantResponse.builder()
                .id(tenant.getId())
                .tenantCode(tenant.getTenantCode())
                .name(tenant.getName())
                .clinicType(tenant.getClinicType())
                .state(tenant.getState())
                .stateCode(tenant.getStateCode())
                .city(tenant.getCity())
                .pinCode(tenant.getPinCode())
                .addressLine1(tenant.getAddressLine1())
                .addressLine2(tenant.getAddressLine2())
                .registrationNumberGst(tenant.getRegistrationNumberGst())
                .logoUrl(tenant.getLogoUrl())
                .fullName(tenant.getFullName())
                .mobileNumber(tenant.getMobileNumber())
                .email(tenant.getEmail())
                .mailEmail(mail != null ? mail.getFromEmail() : null)
                .mailProvider(mail != null ? mail.getProvider() : null)
                .mailPasswordMasked(mail != null
                        ? SecretMasking.mask(secretEncryption.decrypt(mail.getSmtpPassword()))
                        : null)
                .photoUrl(tenant.getPhotoUrl())
                .schemaName(tenant.getSchemaName())
                .platform(tenant.getPlatform())
                .status(tenant.getStatus())
                .provisionMessage(tenant.getProvisionMessage())
                .build();
    }

    public UserResponse toUserResponse(AuthUser user, List<String> pageCodes) {
        if (user == null) {
            return null;
        }
        TenantRole tenantRole = user.getTenantRole();
        return UserResponse.builder()
                .id(user.getId())
                .tenantId(user.getTenant().getId())
                .tenantCode(user.getTenant().getTenantCode())
                .schemaName(user.getTenant().getSchemaName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .mobileNumber(user.getMobileNumber())
                .role(user.getRole())
                .tenantRoleId(tenantRole != null ? tenantRole.getId() : null)
                .tenantRoleCode(tenantRole != null ? tenantRole.getRoleCode() : null)
                .tenantRoleName(tenantRole != null ? tenantRole.getRoleName() : null)
                .pageCodes(pageCodes != null ? pageCodes : List.of())
                .status(user.getStatus())
                .build();
    }

    public UserResponse toUserResponse(AuthUser user) {
        return toUserResponse(user, List.of());
    }

    public UiPageResponse toUiPageResponse(UiPage page) {
        return UiPageResponse.builder()
                .id(page.getId())
                .pageCode(page.getPageCode())
                .pageName(page.getPageName())
                .description(page.getDescription())
                .module(page.getModule())
                .sortOrder(page.getSortOrder())
                .build();
    }

    public TenantRoleResponse toTenantRoleResponse(
            TenantRole role, List<String> pageCodes, Long userCount) {
        return TenantRoleResponse.builder()
                .id(role.getId())
                .tenantId(role.getTenant().getId())
                .roleCode(role.getRoleCode())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .systemRole(role.getSystemRole())
                .active(role.getActive())
                .pageCodes(pageCodes != null ? pageCodes : List.of())
                .userCount(userCount != null ? userCount : 0L)
                .build();
    }

    public TenantRoleResponse toTenantRoleResponse(TenantRole role, List<String> pageCodes) {
        return toTenantRoleResponse(role, pageCodes, 0L);
    }

    public HospitalMailResponse toHospitalMailResponse(Tenant hospital, TenantMailSettings mail) {
        if (hospital == null || mail == null) {
            return null;
        }
        return HospitalMailResponse.builder()
                .hospitalId(hospital.getId())
                .tenantCode(hospital.getTenantCode())
                .email(mail.getFromEmail())
                .provider(mail.getProvider())
                .passwordMasked(SecretMasking.mask(secretEncryption.decrypt(mail.getSmtpPassword())))
                .enabled(mail.getEnabled())
                .updatedAt(mail.getUpdatedAt())
                .build();
    }

    public TenantPaymentGatewayResponse toPaymentGatewayResponse(TenantPaymentGateway gateway) {
        if (gateway == null) {
            return null;
        }
        return TenantPaymentGatewayResponse.builder()
                .tenantCode(gateway.getTenantCode())
                .provider(gateway.getProvider())
                .merchantKey(gateway.getMerchantKey())
                .merchantSaltMasked(SecretMasking.mask(gateway.getMerchantSalt()))
                .clientId(gateway.getClientId())
                .clientSecretMasked(SecretMasking.mask(gateway.getClientSecret()))
                .mode(gateway.getMode())
                .paymentUrl(gateway.getPaymentUrl())
                .resolvedPaymentUrl(resolvePaymentUrl(gateway))
                .enabled(gateway.getEnabled())
                .updatedAt(gateway.getUpdatedAt())
                .build();
    }

    static String resolvePaymentUrl(TenantPaymentGateway gateway) {
        if (gateway.getPaymentUrl() != null && !gateway.getPaymentUrl().isBlank()) {
            return gateway.getPaymentUrl().trim();
        }
        return gateway.getMode() == PaymentGatewayMode.LIVE
                ? "https://secure.payu.in/_payment"
                : "https://test.payu.in/_payment";
    }

}
