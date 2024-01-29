package com.nayoon.preordersystem.auth.service;

import com.nayoon.preordersystem.auth.dto.TokenDto;
import com.nayoon.preordersystem.auth.dto.request.LoginRequest;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.redis.service.RedisService;
import com.nayoon.preordersystem.common.utils.EncryptionUtils;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  /**
   * 사용자 로그인 메서드
   */
  public TokenDto login(LoginRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD));

    if (!EncryptionUtils.matchPassword(request.password(), user.getPassword())) {
      throw new CustomException(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD);
    }

    if (!user.getVerified()) {
      throw new CustomException(ErrorCode.MUST_VERIFIED_EMAIL);
    }

    String accessToken = jwtTokenProvider.generateAccessToken(user);
    String refreshToken = jwtTokenProvider.generateRefreshToken(user);

    Long expiresTime = jwtTokenProvider.getExpiredTime(refreshToken);
    redisService.setValues("RT:" + user.getEmail(), refreshToken, expiresTime, TimeUnit.MILLISECONDS);

    return new TokenDto(accessToken, refreshToken, expiresTime);
  }

}
