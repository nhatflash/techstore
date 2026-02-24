package com.prm292.techstore.services;

import com.prm292.techstore.models.DeviceToken;
import com.prm292.techstore.repositories.DeviceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void registerToken(String username, String token) {
        boolean alreadyExists = deviceTokenRepository.findAllByUsername(username)
                .stream()
                .anyMatch(dt -> dt.getToken().equals(token));

        if (alreadyExists) return;

        DeviceToken deviceToken = DeviceToken.builder()
                .username(username)
                .token(token)
                .build();

        deviceTokenRepository.save(deviceToken);
    }

    public void removeToken(String username, String token) {
        deviceTokenRepository.deleteByUsernameAndToken(username, token);
    }
}
