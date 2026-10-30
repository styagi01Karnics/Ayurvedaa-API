package com.ayurveda.notification.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.constant.NotificationMessages;
import com.ayurveda.notification.dto.request.RegisterDeviceTokenRequest;
import com.ayurveda.notification.entity.DeviceToken;
import com.ayurveda.notification.repository.DeviceTokenRepository;
import com.ayurveda.notification.service.DeviceTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceTokenServiceImpl implements DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    @Override
    public ApiResponse<Void> registerDeviceToken(RegisterDeviceTokenRequest request) {
        Optional<DeviceToken> existing = deviceTokenRepository.findByToken(request.getToken().trim());

        if (existing.isPresent()) {
            DeviceToken device = existing.get();
            device.setUserId(request.getUserId());
            device.setPlatform(request.getPlatform());
            device.setDeleted(false);
            deviceTokenRepository.save(device);
        } else {
            deviceTokenRepository.save(DeviceToken.builder()
                    .userId(request.getUserId())
                    .token(request.getToken().trim())
                    .platform(request.getPlatform())
                    .build());
        }
        log.info("Device token registered for userId={}", request.getUserId());
        return ApiResponse.success(NotificationMessages.DEVICE_TOKEN_REGISTERED, null);
    }

    @Override
    public ApiResponse<Void> removeDeviceToken(String token) {
        deviceTokenRepository.findByToken(token).ifPresent(device -> {
            device.setDeleted(true);
            deviceTokenRepository.save(device);
        });
        log.info("Device token removal requested");
        return ApiResponse.success(NotificationMessages.DEVICE_TOKEN_REMOVED, null);
    }
}
