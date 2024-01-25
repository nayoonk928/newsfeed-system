package com.nayoon.preordersystem.user.service;

import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.common.utils.EncryptionUtils;
import com.nayoon.preordersystem.mail.service.MailService;
import com.nayoon.preordersystem.redis.service.RedisService;
import com.nayoon.preordersystem.s3.service.S3Service;
import com.nayoon.preordersystem.user.dto.request.SignUpRequest;
import com.nayoon.preordersystem.user.dto.request.VerifyEmailRequest;
import com.nayoon.preordersystem.user.entity.User;
import com.nayoon.preordersystem.user.repository.UserRepository;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private static final String AUTH_PREFIX = "AuthCode";

  private final UserRepository userRepository;
  private final MailService mailService;
  private final RedisService redisService;
  private final S3Service s3Service;

  private final String IMAGE_PATH = "image/";

  @Value("${spring.mail.auth-code-expiration-millis}")
  private long authCodeExpirationMillis;

  /**
   * 회원가입 메서드
   */
  @Transactional
  public void signup(SignUpRequest request, MultipartFile imageFile) throws IOException {
    // 비밀번호 암호화
    String encryptPassword = EncryptionUtils.encode(request.password());

    // 이메일 중복 확인
    checkDuplicatedEmail(request.email());

    User user = User.builder()
        .email(request.email())
        .name(request.name())
        .password(encryptPassword)
        .introduction(request.introduction())
        .userRole(request.userRole())
        .verified(false)  // 아직 이메일 인증을 받지 않았기 때문에 verified = true
        .build();

    userRepository.save(user);

    // S3에 업로드
    String s3ImageUrl = s3Service.saveFile(imageFile, user, IMAGE_PATH);

    // s3에 올라간 이미지 URL 저장
    user.updateProfileImage(s3ImageUrl);

    // 이메일로 인증 코드 전송
    sendCode(request.email());
  }

  /**
   * 이메일 중복 체크하는 메서드
   */
  private void checkDuplicatedEmail(String email) {
    if (userRepository.existsByEmail(email)) {
      log.debug("UserService.checkDuplicatedEmail exception occur email: {}", email);
      throw new CustomException(ErrorCode.ALREADY_EXISTS_EMAIL);
    }
  }

  /**
   * 이메일 인증 코드 전송하는 메서드
   */
  public String sendCode(String toEmail) {
    String title = "Preorder 이메일 인증 번호";
    String authCode = createCode();
    mailService.sendEmail(toEmail, title, authCode);

    // key = "AuthCode" + email, value = authCode 형식으로 Redis에 저장
    redisService.setValues(AUTH_PREFIX + toEmail, authCode, Duration.ofMillis(authCodeExpirationMillis));
    return authCode;
  }

  /**
   * 이메일 인증 코드 생성 메서드
   */
  private String createCode() {
    int length = 6;
    try {
      Random random = SecureRandom.getInstanceStrong();
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < length; i++) {
        sb.append(random.nextInt(10));
      }

      return sb.toString();
    } catch (NoSuchAlgorithmException e) {
      log.debug("UserService.createCode exception occur");
      throw new RuntimeException(e);
    }
  }

  /**
   * 이메일 인증 코드 유효성 확인 메서드
   */
  @Transactional
  public boolean verifyCode(VerifyEmailRequest request) {
    boolean authCodeExists = redisService.checkEmailAuthCode(AUTH_PREFIX + request.email(), request.code());

    if (authCodeExists) {
      redisService.deleteKey(AUTH_PREFIX + request.email());

      User user = userRepository.findByEmail(request.email())
          .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

      if (user.getVerified()) {
        throw new CustomException(ErrorCode.ALREADY_VERIFIED_USER);
      }

      user.updateVerified(true);
      return true;
    }

    return false;
  }

}
