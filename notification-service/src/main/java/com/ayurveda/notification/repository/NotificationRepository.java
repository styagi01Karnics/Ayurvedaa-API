package com.ayurveda.notification.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.enums.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Optional<Notification> findByIdAndDeletedFalse(UUID id);

    @Query("""
            SELECT n FROM Notification n
            WHERE n.deleted = false
              AND n.recipientUserId = :userId
              AND (:unreadOnly = false OR n.read = false)
              AND (:type IS NULL OR n.type = :type)
            ORDER BY n.createdAt DESC
            """)
    List<Notification> findForUser(
            @Param("userId") UUID userId,
            @Param("unreadOnly") boolean unreadOnly,
            @Param("type") NotificationType type);

    long countByRecipientUserIdAndReadFalseAndDeletedFalse(UUID recipientUserId);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true, n.readAt = CURRENT_TIMESTAMP, n.updatedAt = CURRENT_TIMESTAMP
            WHERE n.deleted = false
              AND n.recipientUserId = :userId
              AND n.read = false
            """)
    int markAllReadForUser(@Param("userId") UUID userId);

}
