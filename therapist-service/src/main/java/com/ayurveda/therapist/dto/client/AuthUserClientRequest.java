package com.ayurveda.therapist.dto.client;

import java.util.UUID;

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
public class AuthUserClientRequest {

    private String fullName;
    private String email;
    private String password;
    private String mobileNumber;
    private UUID tenantRoleId;

}
