package com.ayurveda.appointment.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.ayurveda.appointment.dto.response.AppointmentDocumentResponse;
import com.ayurveda.appointment.enums.DocumentType;
import com.ayurveda.appointment.util.AppMessages;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentUploadClient {

    private final RestTemplate restTemplate;

    @Value("${services.file-upload.url}")
    private String fileUploadServiceUrl;

    public AppointmentDocumentResponse uploadDocument(
            UUID bookingId,
            DocumentType documentType,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(AppMessages.DOCUMENT_FILE_REQUIRED);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("bookingId", bookingId);
        body.add("documentType", documentType.name());
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ApiResponse<AppointmentDocumentResponse> response = restTemplate.exchange(
                    fileUploadServiceUrl + "/api/v1/documents/upload",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<AppointmentDocumentResponse>>() {})
                    .getBody();

            if (response == null || response.getData() == null) {
                throw new BadRequestException(
                        response != null && response.getMessage() != null
                                ? response.getMessage()
                                : AppMessages.FAILED_TO_UPLOAD_DOCUMENT);
            }

            return response.getData();
        } catch (BadRequestException ex) {
            throw ex;
        } catch (RestClientException ex) {
            log.error("File-upload service call failed for booking {}", bookingId, ex);
            throw new BadRequestException(AppMessages.UNABLE_TO_UPLOAD_DOCUMENT_SERVICE_UNAVAILABLE);
        }
    }

}
