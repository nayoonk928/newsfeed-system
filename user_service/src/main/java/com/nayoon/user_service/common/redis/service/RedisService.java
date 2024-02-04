package com.nayoon.user_service.common.redis.service;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void setValues(String key, Object value, Long expiration, TimeUnit timeUnit) {
    redisTemplate.opsForValue().set(key, value, expiration, timeUnit);
  }

  public Object getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public boolean keyExists(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  public void deleteKey(String key) {
    redisTemplate.delete(key);
  }

  public boolean checkExistsValue(String value) {
    return !value.equals("false");
  }

}
