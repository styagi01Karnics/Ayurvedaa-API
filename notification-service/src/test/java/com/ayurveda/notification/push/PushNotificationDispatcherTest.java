package com.ayurveda.notification.push;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.ayurveda.notification.entity.DeviceToken;
import com.ayurveda.notification.entity.Notification;
import com.ayurveda.notification.enums.NotificationType;
import com.ayurveda.notification.mapper.NotificationMapper;
import com.ayurveda.notification.repository.DeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;

class PushNotificationDispatcherTest {

    private SimpMessagingTemplate messagingTemplate;
    private DeviceTokenRepository deviceTokenRepository;
    private FirebaseMessaging firebaseMessaging;
    private ObjectProvider<FirebaseMessaging> firebaseProvider;
    private PushNotificationDispatcher dispatcher;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        messagingTemplate = mock(SimpMessagingTemplate.class);
        NotificationMapper notificationMapper = mock(NotificationMapper.class);
        deviceTokenRepository = mock(DeviceTokenRepository.class);
        firebaseMessaging = mock(FirebaseMessaging.class);
        firebaseProvider = mock(ObjectProvider.class);
        when(firebaseProvider.getIfAvailable()).thenReturn(firebaseMessaging);
        dispatcher = new PushNotificationDispatcher(
                messagingTemplate, notificationMapper, deviceTokenRepository, firebaseProvider);
    }

    @Test
    void sendsRealtimeToRecipientAndPushesToEachDeviceToken() throws Exception {
        when(deviceTokenRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of(
                DeviceToken.builder().userId(userId).token("token-a").build(),
                DeviceToken.builder().userId(userId).token("token-b").build()));

        dispatcher.dispatch(notification());

        verify(messagingTemplate).convertAndSendToUser(any(String.class), any(String.class), any());
        verify(firebaseMessaging, times(2)).send(any(Message.class));
    }

    @Test
    void skipsFcmWhenFirebaseDisabled() {
        when(firebaseProvider.getIfAvailable()).thenReturn(null);

        dispatcher.dispatch(notification());

        verify(messagingTemplate).convertAndSendToUser(any(String.class), any(String.class), any());
        verify(deviceTokenRepository, never()).findByUserIdAndDeletedFalse(any());
    }

    @Test
    void softDeletesUnregisteredDeviceToken() throws Exception {
        DeviceToken stale = DeviceToken.builder().userId(userId).token("stale").build();
        when(deviceTokenRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(List.of(stale));
        FirebaseMessagingException unregistered = mock(FirebaseMessagingException.class);
        when(unregistered.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
        when(firebaseMessaging.send(any(Message.class))).thenThrow(unregistered);

        dispatcher.dispatch(notification());

        ArgumentCaptor<DeviceToken> saved = ArgumentCaptor.forClass(DeviceToken.class);
        verify(deviceTokenRepository).save(saved.capture());
        assertTrue(saved.getValue().getDeleted());
    }

    private Notification notification() {
        return Notification.builder()
                .recipientUserId(userId)
                .title("Payment successful")
                .message("PayU payment succeeded.")
                .type(NotificationType.BILLING)
                .build();
    }
}
