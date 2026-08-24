package com.ayurveda.auth.dto.response;

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
public class UiPageResponse {

    private UUID id;
    private String pageCode;
    private String pageName;
    private String description;
    private String module;
    private Integer sortOrder;

}
