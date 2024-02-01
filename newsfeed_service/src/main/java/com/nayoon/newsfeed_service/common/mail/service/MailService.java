package com.nayoon.newsfeed_service.common.mail.service;

import com.nayoon.newsfeed_service.common.exception.CustomException;
import com.nayoon.newsfeed_service.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender mailSender;

  /**
   * 이메일 전송 메서드
   */
  public void sendEmail(String toEmail, String title, String text) {
    try {
      SimpleMailMessage email = createEmailForm(toEmail, title, text);
      mailSender.send(email);
    } catch (RuntimeException e) {
      log.debug("MailService.sendEmail exception occur email: {}, title: {}, text: {}", toEmail, title, text);
      throw new CustomException(ErrorCode.UNABLE_TO_SEND_EMAIL);
    }
  }

  /**
   * 이메일 형식 작성 메서드
   */
  private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject(title);
    message.setText(text);

    return message;
  }

}
