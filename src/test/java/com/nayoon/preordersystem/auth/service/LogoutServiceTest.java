package com.nayoon.preordersystem.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.redis.service.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LogoutServiceTest {

  @InjectMocks
  private LogoutService logoutService;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private RedisService redisService;

  @Nested
  @DisplayName("로그아웃")
  class logout {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      String accessToken = jwtTokenProvider.resolveAccessToken(request);

      when(jwtTokenProvider.validateToken(accessToken)).thenReturn(true);

      AbstractAuthenticationToken authentication = mock(AbstractAuthenticationToken.class);
      when(authentication.getName()).thenReturn("authenticatedUserEmail");
      when(jwtTokenProvider.getAuthentication(accessToken)).thenReturn(authentication);

      //when
      logoutService.logout(request);

      //then
      verify(jwtTokenProvider, times(1)).validateToken(accessToken);
      verify(jwtTokenProvider, times(1)).getAuthentication(accessToken);
      verify(redisService, times(1)).setValues(eq(accessToken), eq(null),
          anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("실패: 유효하지 않은 토큰")
    void invalidToken() {
      //given
      HttpServletRequest request = mock(HttpServletRequest.class);
      String accessToken = jwtTokenProvider.resolveAccessToken(request);
      when(jwtTokenProvider.validateToken(accessToken)).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> logoutService.logout(request));

      //then
      assertEquals(ErrorCode.INVALID_AUTHENTICATION_TOKEN, exception.getErrorCode());
    }

  }

}