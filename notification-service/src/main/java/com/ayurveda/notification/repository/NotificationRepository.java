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

    /** Notifications addressed to the user, plus role-wide notifications for the user's role. */
    @Query("""
            SELECT n FROM Notification n
            WHERE n.deleted = false
              AND (n.recipientUserId = :userId
                   OR (n.recipientUserId IS NULL AND UPPER(n.recipientRole) = UPPER(:roleId)))
              AND (:type IS NULL OR n.type = :type)
            ORDER BY n.createdAt DESC
            """)
    List<Notification> findForRecipient(
            @Param("userId") UUID userId,
            @Param("roleId") String roleId,
            @Param("type") NotificationType type);

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
