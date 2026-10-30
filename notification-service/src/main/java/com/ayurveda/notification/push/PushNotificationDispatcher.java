package com.ayurveda.notification.push;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.ayurveda.notification.entity.DeviceToken;
import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.mapper.NotificationMapper;
import com.ayurveda.notification.repository.DeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Delivers a saved notification to the recipient over WebSocket (STOMP) and, when Firebase
 * is enabled, to each of the user's registered device tokens. Failures are logged and never
 * propagate to the caller.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PushNotificationDispatcher {

    public static final String USER_QUEUE_DESTINATION = "/queue/notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationMapper notificationMapper;
    private final DeviceTokenRepository deviceTokenRepository;
    private final ObjectProvider<FirebaseMessaging> firebaseMessaging;

    public void dispatch(Notification notification) {
        if (notification.getRecipientUserId() == null) {
            return;
        }
        sendRealtime(notification);
        sendDevicePush(notification);
    }

    private void sendRealtime(Notification notification) {
        try {
            messagingTemplate.convertAndSendToUser(
                    notification.getRecipientUserId().toString(),
                    USER_QUEUE_DESTINATION,
                    notificationMapper.toResponse(notification));
        } catch (Exception ex) {
            log.warn("Real-time notification delivery failed. userId={}, error={}",
                    notification.getRecipientUserId(), ex.getMessage());
        }
    }

    private void sendDevicePush(Notification notification) {
        FirebaseMessaging fcm = firebaseMessaging.getIfAvailable();
        if (fcm == null) {
            return;
        }
        List<DeviceToken> devices =
                deviceTokenRepository.findByUserIdAndDeletedFalse(notification.getRecipientUserId());

        for (DeviceToken device : devices) {
            try {
                Message message = Message.builder()
                        .setToken(device.getToken())
                        .setNotification(com.google.firebase.messaging.Notification.builder()
                                .setTitle(notification.getTitle())
                                .setBody(notification.getMessage())
                                .build())
                        .build();
                fcm.send(message);
            } catch (FirebaseMessagingException ex) {
                if (ex.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED
                        || ex.getMessagingErrorCode() == MessagingErrorCode.INVALID_ARGUMENT) {
                    device.setDeleted(true);
                    deviceTokenRepository.save(device);
                }
                log.warn("FCM push failed. userId={}, errorCode={}",
                        notification.getRecipientUserId(), ex.getMessagingErrorCode());
            }
        }
    }
}
