package com.nayoon.preordersystem.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.auth.dto.request.LogoutRequest;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.redis.service.RedisService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
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
      LogoutRequest request = createLogoutRequest();
      when(jwtTokenProvider.validateToken(request.accessToken())).thenReturn(true);

      Authentication authentication = mock(Authentication.class);
      when(authentication.getName()).thenReturn("authenticatedUserEmail");
      when(jwtTokenProvider.getAuthentication(request.accessToken())).thenReturn(authentication);

      //when
      logoutService.logout(request);

      //then
      verify(jwtTokenProvider, times(1)).validateToken(request.accessToken());
      verify(jwtTokenProvider, times(1)).getAuthentication(request.accessToken());
      verify(redisService, times(1)).setValues(eq(request.accessToken()), eq("logout"),
          anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("실패: 유효하지 않은 토큰")
    void invalidToken() {
      //given
      LogoutRequest request = createLogoutRequest();
      when(jwtTokenProvider.validateToken(request.accessToken())).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> logoutService.logout(request));

      //then
      assertEquals(ErrorCode.INVALID_AUTHENTICATION_TOKEN, exception.getErrorCode());
    }

  }

  public LogoutRequest createLogoutRequest() {
    return new LogoutRequest("accessToken", "refreshToken");
  }

}