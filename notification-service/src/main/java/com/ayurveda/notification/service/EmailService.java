package com.ayurveda.notification.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.SendEmailRequest;
import com.ayurveda.notification.dto.response.EmailSendResponse;

public interface EmailService {

    /**
     * Attempts SMTP delivery with retries. Never throws for delivery failures —
     * always returns HTTP-friendly ApiResponse (success=true) so callers never fail.
     */
    ApiResponse<EmailSendResponse> sendEmail(SendEmailRequest request);
}
