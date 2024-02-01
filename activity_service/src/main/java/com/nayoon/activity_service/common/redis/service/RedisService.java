package com.nayoon.activity_service.common.redis.service;

import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
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

  public boolean checkEmailAuthCode(String key, String value) {
    String storedValue = (String) redisTemplate.opsForValue().get(key);

    if (storedValue == null) {
      throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
    }

    if (!storedValue.equals(value)) {
      throw new CustomException(ErrorCode.EMAIL_AUTH_CODE_INCORRECT);
    }

    return true;
  }

  public boolean checkExistsValue(String value) {
    return !value.equals("false");
  }

}
