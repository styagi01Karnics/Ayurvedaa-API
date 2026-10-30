package com.ayurveda.notification.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.dto.request.RegisterDeviceTokenRequest;

public interface DeviceTokenService {

    ApiResponse<Void> registerDeviceToken(RegisterDeviceTokenRequest request);

    ApiResponse<Void> removeDeviceToken(String token);
}
