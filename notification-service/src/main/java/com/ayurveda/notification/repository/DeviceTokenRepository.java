package com.ayurveda.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ayurveda.notification.entity.DeviceToken;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, UUID> {

    Optional<DeviceToken> findByToken(String token);

    List<DeviceToken> findByUserIdAndDeletedFalse(UUID userId);

}
