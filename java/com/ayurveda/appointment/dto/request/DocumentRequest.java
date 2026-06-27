package com.ayurveda.appointment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {

    private String documentType;

    private String fileName;

    private String contentType;

}