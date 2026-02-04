package com.prm292.techstore.utils;

import com.prm292.techstore.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public String getOrderPrefixKey(long orderCode) {
        return "order-" + orderCode + ":";
    }

    public void saveIntToString(String key, int value) {
        redisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    public int getIntFromString(String key) {
        String valueStr = (String)redisTemplate.opsForValue().get(key);
        if (valueStr == null) {
            return -1;
        }
        return Integer.parseInt(valueStr);
    }

    public void removeItem(String key) {
        redisTemplate.delete(key);
    }
}
