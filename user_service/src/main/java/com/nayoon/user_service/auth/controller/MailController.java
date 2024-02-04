package com.nayoon.user_service.auth.controller;

import com.nayoon.user_service.auth.dto.request.MailRequest;
import com.nayoon.user_service.auth.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class MailController {

  private final MailService mailService;

  /**
   * 인증 메일 전송 컨트롤러
   */
  @PostMapping("/emails")
  public ResponseEntity<String> sendEmail(
      @Valid @RequestBody MailRequest request
  ) {
    return ResponseEntity.ok().body(mailService.sendCode(request));
  }

}
