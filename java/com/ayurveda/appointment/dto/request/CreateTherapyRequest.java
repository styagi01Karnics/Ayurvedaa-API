package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTherapyRequest {

    @NotNull
    private UUID categoryId;

    @NotBlank
    @Size(max = 150)
    private String therapyName;

    @Size(max = 500)
    private String description;

    @Builder.Default
    private Boolean active = true;

}