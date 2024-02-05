package com.nayoon.api_gateway_service.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

  private final RedisTemplate<String, String> redisTemplate;

  public boolean keyExists(String key) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }


}
