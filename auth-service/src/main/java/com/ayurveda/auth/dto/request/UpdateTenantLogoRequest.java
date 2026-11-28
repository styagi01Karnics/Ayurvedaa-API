package com.ayurveda.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateTenantLogoRequest {

    /** PNG image as a base64 data URL, e.g. data:image/png;base64,... */
    @NotBlank
    private String logoUrl;

}
