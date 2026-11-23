package com.ayurveda.auth.dto.request;

import java.util.UUID;

import com.ayurveda.auth.constant.AuthValidation;
import com.ayurveda.auth.enums.Profession;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class UpdateUserRequest {

    @Size(max = 100)
    private String fullName;

    /** Optional new Gmail login identity. */
    @Size(max = 150)
    @Pattern(regexp = AuthValidation.GMAIL, message = AuthValidation.GMAIL_MESSAGE)
    private String email;

    /** Accepted only when it matches the current code; user codes cannot be changed. */
    private String userCode;

    private Profession profession;

    private UserRole role;

    private UUID tenantRoleId;

    private UserStatus status;

}
