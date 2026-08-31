package com.ayurveda.notification.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.SendEmailRequest;

public interface EmailService {

    ApiResponse<Void> sendEmail(SendEmailRequest request);

}
