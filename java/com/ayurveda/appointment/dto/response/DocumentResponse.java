package com.ayurveda.appointment.dto.response;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {

    private UUID documentId;

    private String documentType;

    private String fileName;

    private String contentType;

    private Long fileSize;

}