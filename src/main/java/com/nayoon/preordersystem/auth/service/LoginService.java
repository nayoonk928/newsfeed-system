package com.nayoon.preordersystem.auth.service;

import com.nayoon.preordersystem.auth.dto.TokenDto;
import com.nayoon.preordersystem.auth.dto.request.SignInRequest;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.utils.EncryptionUtils;
import com.nayoon.preordersystem.redis.service.RedisService;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final EncryptionUtils encryptionUtils;
  private final RedisService redisService;

  /**
   * 사용자 로그인 메서드
   */
  public TokenDto login(SignInRequest request) {
    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD));

    if (!encryptionUtils.matchPassword(request.password(), user.getPassword())) {
      throw new CustomException(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD);
    }

    TokenDto tokenDto = jwtTokenProvider.generateToken(user.getEmail(), user.getName(),
        user.getUserRole());

    redisService.setValues("RT:" + user.getEmail(), tokenDto.refreshToken(),
        tokenDto.refreshTokenExpiresTime(), TimeUnit.MILLISECONDS);

    return tokenDto;
  }

}