package com.nayoon.activity_service.auth.service;

import com.nayoon.activity_service.common.exception.CustomException;
import com.nayoon.activity_service.common.exception.ErrorCode;
import com.nayoon.activity_service.common.redis.service.RedisService;
import com.nayoon.activity_service.user.entity.User;
import com.nayoon.activity_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserRepository userRepository;
  private final RedisService redisService;

  /**
   * accessToken 만료시 refreshToken이 있다면 재발급해주는 메서드
   */
  @Transactional
  public String refresh(HttpServletRequest request) {
    // redis에 refresh 토큰이 있는지 확인
    String refreshToken = jwtTokenProvider.resolveRefreshToken(request);
    String email = jwtTokenProvider.getEmail(refreshToken);

    String redisRefreshToken = (String) redisService.getValue("RT:" + email);
    if (!redisService.checkExistsValue(redisRefreshToken)) {
      throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }

    // 헤더의 refreshToken과 redis의 refreshToken이 일치하는지 확인
    if (refreshToken.equals(redisRefreshToken)) {

      User user = userRepository.findByEmail(email)
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      // accessToken 생성 및 반환
      return jwtTokenProvider.generateAccessToken(user);
    } else {
      throw new CustomException(ErrorCode.TOKEN_IS_NOT_SAME);
    }
  }

}
