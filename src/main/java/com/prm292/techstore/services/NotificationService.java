package com.prm292.techstore.services;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.prm292.techstore.models.DeviceToken;
import com.prm292.techstore.repositories.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
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
}
