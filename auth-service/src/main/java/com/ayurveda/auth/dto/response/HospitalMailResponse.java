package com.ayurveda.auth.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.auth.enums.HospitalMailProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Super Admin view of hospital sending mailbox. Password is masked. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalMailResponse {

    private UUID hospitalId;
    private String tenantCode;
    private String email;
    private HospitalMailProvider provider;
    private String passwordMasked;
    private Boolean enabled;
    private LocalDateTime updatedAt;

}
