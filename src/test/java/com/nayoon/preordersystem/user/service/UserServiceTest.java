package com.nayoon.preordersystem.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nayoon.preordersystem.auth.security.CustomUserDetails;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.mail.service.MailService;
import com.nayoon.preordersystem.common.redis.service.RedisService;
import com.nayoon.preordersystem.common.s3.service.S3Service;
import com.nayoon.preordersystem.common.utils.EncryptionUtils;
import com.nayoon.preordersystem.user.dto.request.PasswordUpdateRequest;
import com.nayoon.preordersystem.user.dto.request.ProfileUpdateRequest;
import com.nayoon.preordersystem.user.dto.request.SignUpRequest;
import com.nayoon.preordersystem.user.dto.request.VerifyEmailRequest;
import com.nayoon.preordersystem.user.dto.response.UserResponse;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import com.nayoon.preordersystem.user.type.UserRole;
import java.io.IOException;
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
      verify(redisService, times(1)).setValues(anyString(), anyString(), anyLong(),
          eq(TimeUnit.MILLISECONDS));
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

  @Nested
  @DisplayName("사용자 정보 업데이트")
  class updateProfile {

    @Test
    @DisplayName("성공")
    void success() throws IOException {
      //given
      CustomUserDetails userDetails = createCustomUserDetails();
      ProfileUpdateRequest updateRequest = createProfileUpdateRequest();
      MultipartFile imageFile = createMultipartFile();

      when(userRepository.findById(eq(userDetails.getId()))).thenReturn(
          Optional.of(createUser(true)));

      //when
      UserResponse result = userService.updateProfile(userDetails, updateRequest, imageFile);

      //then
      verify(userRepository, times(1)).findById(eq(userDetails.getId()));

      assertEquals("test@example.com", result.email());
      assertEquals(updateRequest.name(), result.name());
      assertEquals(updateRequest.greeting(), result.greeting());
    }

    @Test
    @DisplayName("실패: 사용자 찾을 수 없음")
    void userNotFound() {
      //given
      CustomUserDetails userDetails = createCustomUserDetails();
      ProfileUpdateRequest updateRequest = createProfileUpdateRequest();
      MultipartFile imageFile = createMultipartFile();

      when(userRepository.findById(eq(userDetails.getId()))).thenReturn(Optional.empty());

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> userService.updateProfile(userDetails, updateRequest, imageFile));

      //then
      assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

  }

  @Nested
  @DisplayName("비밀번호 업데이트")
  class updatePassword {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      CustomUserDetails userDetails = createCustomUserDetails();
      PasswordUpdateRequest request = createPasswordUpdateRequest();
      User user = createUser(true);

      when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(user));
      when(EncryptionUtils.matchPassword(request.prevPassword(), user.getPassword())).thenReturn(
          true);
      when(EncryptionUtils.matchPassword(request.prevPassword(), request.newPassword())).thenReturn(
          false);

      //when
      userService.updatePassword(userDetails, request);

      //then
      verify(userRepository, times(1)).findById(eq(userDetails.getId()));
    }

    @Test
    @DisplayName("실패: 현재 비밀번호를 잘못 입력")
    void notMatchedCurrPassword() {
      //given
      CustomUserDetails userDetails = createCustomUserDetails();
      PasswordUpdateRequest request = createPasswordUpdateRequest();
      User user = createUser(true);

      when(userRepository.findById(userDetails.getId())).thenReturn(Optional.of(user));
      when(EncryptionUtils.matchPassword(request.prevPassword(), user.getPassword())).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> userService.updatePassword(userDetails, request));

      //then
      assertEquals(ErrorCode.NOT_MATCHED_CURR_PASSWORD, exception.getErrorCode());
    }

  }

  private SignUpRequest createSignUpRequest() {
    return SignUpRequest.builder()
        .email("test@example.com")
        .name("Test User")
        .password("password")
        .greeting("Hello, I'm a test user.")
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
        .greeting("Hello, I'm a test user.")
        .profileImage("s3///image/jpeg")
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

  private CustomUserDetails createCustomUserDetails() {
    return new CustomUserDetails(createUser(true));
  }

  private ProfileUpdateRequest createProfileUpdateRequest() {
    return new ProfileUpdateRequest("김길동", "Hello");
  }

  private PasswordUpdateRequest createPasswordUpdateRequest() {
    return new PasswordUpdateRequest("encoded_password", "newPassword");
  }

}