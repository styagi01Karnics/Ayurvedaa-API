package com.ayurveda.auth.dto.response;

import java.util.UUID;

import com.ayurveda.auth.enums.TenantStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {

    private UUID id;
    private String tenantCode;
    private String name;
    private String clinicType;
    private String state;
    private String stateCode;
    private String city;
    private String pinCode;
    private String addressLine1;
    private String addressLine2;
    private String registrationNumberGst;
    private String logoUrl;
    /** Hospital primary contact full name. */
    private String fullName;
    private String mobileNumber;
    private String email;
    /** Primary contact / admin photo URL. */
    private String photoUrl;
    private String schemaName;
    private Boolean platform;
    private TenantStatus status;
    private String provisionMessage;

}
