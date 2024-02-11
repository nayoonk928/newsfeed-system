package com.nayoon.user_service.auth.service;

import com.nayoon.user_service.auth.dto.request.MailRequest;
import com.nayoon.user_service.common.redis.service.RedisService;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

  private static final String AUTH_PREFIX = "AuthCode";

  private final JavaMailSender mailSender;
  private final RedisService redisService;

  @Value("${spring.mail.auth-code-expiration-millis}")
  private long authCodeExpirationMillis;

  /**
   * 이메일 인증 코드 전송하는 메서드
   */
  public String sendCode(MailRequest request) {
    String toEmail = request.email();
    String title = "Preorder 이메일 인증 번호";
    String authCode = createCode();
    SimpleMailMessage email = createEmailForm(toEmail, title, authCode);
    mailSender.send(email);

    // key = "AuthCode" + email, value = authCode 형식으로 Redis에 저장
    redisService.setValues(AUTH_PREFIX + toEmail, authCode, authCodeExpirationMillis, TimeUnit.MILLISECONDS);
    return authCode;
  }

  // 이메일 인증 코드 생성 메서드
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

  // 이메일 형식 작성 메서드
  private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject(title);
    message.setText(text);

    return message;
  }

}
