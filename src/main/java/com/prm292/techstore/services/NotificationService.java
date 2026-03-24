package com.prm292.techstore.services;

import com.google.firebase.messaging.*;
import com.prm292.techstore.models.DeviceToken;
import com.prm292.techstore.repositories.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void updateCartBadge(String username, int cartQuantity) {
        List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByUsername(username);
        if (deviceTokens.isEmpty()) return;

        for (DeviceToken deviceToken : deviceTokens) {
            updateCartItemBadge(deviceToken.getToken(), cartQuantity);
        }
    }

    private void updateCartItemBadge(String fcmToken, int cartQuantity) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setBadge(cartQuantity)
                                .setContentAvailable(true)
                                .build())
                        .build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setNotification(AndroidNotification.builder()
                                .setNotificationCount(cartQuantity)
                                .build())
                        .build())
                .build();

        FirebaseMessaging.getInstance().sendAsync(message);
    }

    @Async
    public void sendChatNotification(List<String> fcmTokens, String receiverName, String senderName, String messageBody, int roomId) {
        if (fcmTokens.isEmpty()) return;
        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(fcmTokens)
                .putData("title", "Tin nhắn từ " + senderName)
                .putData("body", messageBody)
                .putData("roomId", String.valueOf(roomId))
                .putData("clientName", receiverName)
                .build();
        try {
            var response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            if (response.getFailureCount() > 0) {
                System.out.println("Failed to send message to " + response.getFailureCount() + " tokens");
            }
        } catch (Exception e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
