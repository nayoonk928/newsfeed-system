package com.nayoon.preordersystem.auth.service;

import com.nayoon.preordersystem.auth.dto.request.LogoutRequest;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.redis.service.RedisService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  @Transactional
  public void logout(LogoutRequest request) {
    String accessToken = request.accessToken();

    if (!jwtTokenProvider.validateToken(accessToken)) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }

    Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

    // Redis에 현재 사용자의 이메일로 저장된 refreshToken이 있으면 삭제
    String redisKey = "RT:" + authentication.getName();
    if (redisService.getValue(redisKey) != null) {
      redisService.deleteKey(redisKey);
    }

    // 해당 accessToken 유효시간을 블랙리스트에 저장
    Long expiration = jwtTokenProvider.getAccessTokenExpiration(accessToken);
    redisService.setValues(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
  }

}
