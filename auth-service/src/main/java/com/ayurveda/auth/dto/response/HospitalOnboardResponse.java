package com.ayurveda.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Hospital onboard result: clinic + first hospital admin. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HospitalOnboardResponse {

    private TenantResponse hospital;
    private UserResponse admin;

}
