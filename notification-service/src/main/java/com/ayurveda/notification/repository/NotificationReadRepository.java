package com.ayurveda.notification.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.notification.entity.NotificationRead;

public interface NotificationReadRepository extends JpaRepository<NotificationRead, UUID> {

    @Query("SELECT r.notificationId FROM NotificationRead r WHERE r.userId = :userId AND r.deleted = false")
    Set<UUID> findReadNotificationIdsByUserId(@Param("userId") UUID userId);
}
