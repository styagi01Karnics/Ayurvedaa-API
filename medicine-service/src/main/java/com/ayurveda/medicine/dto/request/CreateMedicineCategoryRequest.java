package com.ayurveda.medicine.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class CreateMedicineCategoryRequest {

    @NotBlank(message = "Category code is required")
    @Size(max = 30)
    @Pattern(regexp = "^[A-Z][A-Z0-9_]*$", message = "Category code must be uppercase letters, digits or underscore")
    private String code;

    @NotBlank(message = "Category name is required")
    @Size(max = 100)
    private String name;

}
