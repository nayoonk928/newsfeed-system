package com.nayoon.user_service.auth.service;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.user_service.auth.dto.TokenDto;
import com.nayoon.user_service.auth.dto.request.LoginRequest;
import com.nayoon.user_service.common.exception.CustomException;
import com.nayoon.user_service.common.exception.ErrorCode;
import com.nayoon.user_service.common.redis.service.RedisService;
import com.nayoon.user_service.common.utils.EncryptionUtils;
import com.nayoon.user_service.user.entity.User;
import com.nayoon.user_service.user.repository.UserRepository;
import com.nayoon.user_service.user.type.UserRole;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class LoginServiceTest {

  @InjectMocks
  private LoginService loginService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtTokenProvider jwtTokenProvider;

  @Mock
  private RedisService redisService;

  private static MockedStatic<EncryptionUtils> mEncryptionUtils;

  @BeforeAll
  static void beforeClass() {
    mEncryptionUtils = mockStatic(EncryptionUtils.class);
  }

  @AfterAll
  static void afterClass() {
    mEncryptionUtils.close();
  }

  @Nested
  @DisplayName("로그인")
  class login {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      User user = createUser();
      LoginRequest request = createLoginRequest();
      TokenDto tokenDto = createTokenDto();

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
      when(EncryptionUtils.matchPassword(request.password(), user.getPassword())).thenReturn(true);
      when(jwtTokenProvider.generateAccessToken(user)).thenReturn(tokenDto.accessToken());
      when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(tokenDto.refreshToken());
      when(jwtTokenProvider.getExpiredTime(tokenDto.refreshToken())).thenReturn(
          tokenDto.refreshTokenExpiresTime());

      //when
      TokenDto result = loginService.login(request);

      //then
      assertNotNull(result);
      assertEquals(tokenDto, result);
      verify(userRepository, times(1)).findByEmail(user.getEmail());
      verify(redisService, times(1)).setValues(eq("RT:" + user.getEmail()),
          eq(tokenDto.refreshToken()), eq(tokenDto.refreshTokenExpiresTime()),
          eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("실패: 해당 이메일로 가입된 사용자 없음")
    void invalidEmail() {
      //given
      LoginRequest request = createLoginRequest();

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> loginService.login(request));

      //then
      assertEquals(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 비밀번호 다름")
    void invalidPassword() {
      //given
      User user = createUser();
      LoginRequest request = createInvalidLoginRequest();

      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
      when(EncryptionUtils.matchPassword(request.password(), user.getPassword())).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> loginService.login(request));

      //then
      assertEquals(ErrorCode.INCORRECT_EMAIL_OR_PASSWORD, exception.getErrorCode());
    }

  }

  private LoginRequest createLoginRequest() {
    return new LoginRequest("test@example.com", "password");
  }

  private LoginRequest createInvalidLoginRequest() {
    return new LoginRequest("test@example.com", "passwor");
  }

  private User createUser() {
    return User.builder()
        .email("test@example.com")
        .name("Test User")
        .password("password")
        .greeting("Hello, I'm a test user.")
        .userRole(UserRole.USER)
        .build();
  }

  private TokenDto createTokenDto() {
    return new TokenDto("accessToken", "refreshToken", 1000L);
  }

}