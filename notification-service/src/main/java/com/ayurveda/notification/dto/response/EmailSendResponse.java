package com.ayurveda.notification.dto.response;

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
public class EmailSendResponse {

    /** SENT, FAILED, or SKIPPED */
    private String deliveryStatus;
    private int attempts;
    private String to;
    private String tenantCode;
    /** Present when deliveryStatus=FAILED */
    private String errorMessage;

}
