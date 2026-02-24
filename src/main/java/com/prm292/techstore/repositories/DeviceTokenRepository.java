package com.prm292.techstore.repositories;

import com.prm292.techstore.models.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {

    List<DeviceToken> findAllByUsername(String username);

    void deleteByUsernameAndToken(String username, String token);
}
