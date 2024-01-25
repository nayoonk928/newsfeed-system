package com.nayoon.preordersystem.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.mail.service.MailService;
import com.nayoon.preordersystem.redis.service.RedisService;
import com.nayoon.preordersystem.s3.service.S3Service;
import com.nayoon.preordersystem.user.dto.request.SignUpRequest;
import com.nayoon.preordersystem.user.dto.request.VerifyEmailRequest;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import com.nayoon.preordersystem.user.type.UserRole;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private MailService mailService;

  @Mock
  private RedisService redisService;

  @Mock
  private S3Service s3Service;

  @Nested
  @DisplayName("회원가입")
  class SignUp {

    @Test
    @DisplayName("성공")
    void success() throws IOException {
      //given
      SignUpRequest request = createSignUpRequest();
      MultipartFile file = createMultipartFile();

      when(userRepository.existsByEmail(request.email())).thenReturn(false);

      //when
      userService.signup(request, file);

      //then
      verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("실패: 중복되는 이메일")
    void duplicateEmail() {
      //given
      SignUpRequest request = createSignUpRequest();
      when(userRepository.existsByEmail(request.email())).thenReturn(true);
      MultipartFile file = createMultipartFile();

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> userService.signup(request, file));

      //then
      assertEquals(ErrorCode.ALREADY_EXISTS_EMAIL, exception.getErrorCode());
    }

  }

  @Nested
  @DisplayName("인증 코드 이메일 전송")
  class SendCode {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      String email = "test@example.com";

      //when
      userService.sendCode(email);

      //then
      verify(mailService, times(1)).sendEmail(eq(email), anyString(), anyString());
      verify(redisService, times(1)).setValues(anyString(), anyString(), any(), TimeUnit.MILLISECONDS);
    }

  }

  @Nested
  @DisplayName("인증 코드 확인")
  class VerifyCode {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      VerifyEmailRequest request = createVerifyEmailRequest();
      when(redisService.checkEmailAuthCode(anyString(), anyString())).thenReturn(true);
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(createUser(false)));

      //when
      userService.verifyCode(request);

      //then
      verify(redisService, times(1)).deleteKey(anyString());
    }

    @Test
    @DisplayName("실패: 입력된 코드가 다름")
    void invalidCode() {
      //given
      VerifyEmailRequest request = createVerifyEmailRequest();
      when(redisService.checkEmailAuthCode(anyString(), anyString())).thenReturn(false);

      //when
      userService.verifyCode(request);

      //then
      verify(redisService, never()).deleteKey(anyString());
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("실패: 사용자를 찾을 수 없음")
    void userNotFound() {
      //given
      VerifyEmailRequest request = createVerifyEmailRequest();
      when(redisService.checkEmailAuthCode(anyString(), anyString())).thenReturn(true);
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> userService.verifyCode(request));

      //then
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
      verify(redisService, times(1)).deleteKey(anyString());
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("예외: 이미 인증된 사용자")
    void alreadyVerifiedUser() {
      //given
      VerifyEmailRequest request = createVerifyEmailRequest();
      when(redisService.checkEmailAuthCode(anyString(), anyString())).thenReturn(true);
      when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(createUser(true)));

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> userService.verifyCode(request));

      //then
      assertEquals(ErrorCode.ALREADY_VERIFIED_USER, exception.getErrorCode());
      verify(redisService, times(1)).deleteKey(anyString());
      verify(userRepository, never()).save(any(User.class));
    }

  }

  private SignUpRequest createSignUpRequest() {
    return SignUpRequest.builder()
        .email("test@example.com")
        .name("Test User")
        .password("password")
        .introduction("Hello, I'm a test user.")
        .userRole(UserRole.USER)
        .build();
  }

  private VerifyEmailRequest createVerifyEmailRequest() {
    return VerifyEmailRequest.builder()
        .email("test@example.com")
        .code("123456")
        .build();
  }

  private User createUser(boolean verified) {
    return User.builder()
        .email("test@example.com")
        .name("Test User")
        .password("encoded_password")
        .introduction("Hello, I'm a test user.")
        .userRole(UserRole.USER)
        .verified(verified)
        .build();
  }

  private MultipartFile createMultipartFile() {
    return new MockMultipartFile(
        "imageFile",
        "test.jpg",
        "image/jpeg",
        "test image content".getBytes());
  }

}