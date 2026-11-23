package com.ayurveda.medicine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ayurveda.common.validation.ValidName;
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
public class UpdateMedicineCategoryRequest {

    @NotBlank(message = "Category name is required")
    @ValidName
    private String name;

    @NotNull(message = "Active flag is required")
    private Boolean active;

}
