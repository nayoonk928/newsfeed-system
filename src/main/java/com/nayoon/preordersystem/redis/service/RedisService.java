package com.nayoon.preordersystem.redis.service;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  public RedisService(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void setValues(String key, Object value, Long expiration, TimeUnit timeUnit) {
    redisTemplate.opsForValue().set(key, value, expiration, timeUnit);
  }

  public Object getValue(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public boolean keyExists(String key) {
    return redisTemplate.hasKey(key);
  }

  public void deleteKey(String key) {
    redisTemplate.delete(key);
  }

  public boolean checkEmailAuthCode(String key, Object value) {
    if (keyExists(key)) {
      Object storedValue = redisTemplate.opsForValue().get(key);
      return storedValue != null && storedValue.equals(value);
    } else {
      throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
    }
  }

}
