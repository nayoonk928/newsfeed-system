package com.nayoon.newsfeed_service.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.newsfeed_service.common.exception.CustomException;
import com.nayoon.newsfeed_service.common.exception.ErrorCode;
import com.nayoon.newsfeed_service.common.redis.service.RedisService;
import com.nayoon.newsfeed_service.user.entity.User;
import com.nayoon.newsfeed_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

  @InjectMocks
  private RefreshTokenService refreshTokenService;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private UserRepository userRepository;

  @Mock
  private RedisService redisService;

  @Nested
  @DisplayName("AccessToken 재발급")
  class refresh {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      User user = mock(User.class);
      HttpServletRequest request = mock(HttpServletRequest.class);
      String refreshToken = "refreshToken";
      String email = "email@example.com";

      when(jwtTokenProvider.resolveRefreshToken(request)).thenReturn(refreshToken);
      when(jwtTokenProvider.getEmail(refreshToken)).thenReturn(email);
      when(redisService.getValue("RT:" + email)).thenReturn(refreshToken);
      when(redisService.checkExistsValue(refreshToken)).thenReturn(true);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(jwtTokenProvider.generateAccessToken(user)).thenReturn("accessToken");

      //when
      String accessToken = refreshTokenService.refresh(request);

      //then
      assertNotNull(accessToken);
      assertEquals("accessToken", accessToken);
      verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("실패: Redis에 토큰 없음")
    void refreshTokenNotFound() {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      String refreshToken = "refreshToken";
      String email = "email@example.com";

      when(jwtTokenProvider.resolveRefreshToken(request)).thenReturn(refreshToken);
      when(jwtTokenProvider.getEmail(refreshToken)).thenReturn(email);
      when(redisService.getValue("RT:" + email)).thenReturn(refreshToken);
      when(redisService.checkExistsValue(refreshToken)).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class, ()
          -> refreshTokenService.refresh(request));

      //then
      assertEquals(ErrorCode.REFRESH_TOKEN_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 사용자 없음")
    void userNotFound() {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      String refreshToken = "refreshToken";
      String email = "email@example.com";

      when(jwtTokenProvider.resolveRefreshToken(request)).thenReturn(refreshToken);
      when(jwtTokenProvider.getEmail(refreshToken)).thenReturn(email);
      when(redisService.getValue("RT:" + email)).thenReturn(refreshToken);
      when(redisService.checkExistsValue(refreshToken)).thenReturn(true);
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> refreshTokenService.refresh(request));

      //then
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

  }

}