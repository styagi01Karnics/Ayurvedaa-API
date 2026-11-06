package com.ayurveda.medicine.dto.response;

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
public class MedicineCategoryResponse {

    private UUID id;
    private String code;
    private String name;
    private Boolean active;

}
