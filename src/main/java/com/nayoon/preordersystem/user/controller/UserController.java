package com.nayoon.preordersystem.user.controller;

import com.nayoon.preordersystem.user.dto.request.SignUpRequest;
import com.nayoon.preordersystem.user.dto.request.VerifyEmailRequest;
import com.nayoon.preordersystem.user.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  /**
   * 회원가입 컨트롤러
   */
  @PostMapping("/signup")
  public ResponseEntity<Void> signup(
      @Valid @RequestPart("data") SignUpRequest request,
      @RequestPart(name = "profileImage") MultipartFile imageFile
  ) throws IOException {
    userService.signup(request, imageFile);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * 인증 메일 전송 컨트롤러
   */
  @PostMapping("/emails")
  public ResponseEntity<Void> sendEmail(
      @Valid @RequestBody String email
  ) {
    userService.sendCode(email);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * 인증 코드 확인 컨트롤러
   */
  @GetMapping("/emails/verifications")
  public ResponseEntity<Void> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest request
  ) {
    boolean isVerified = userService.verifyCode(request);

    if (isVerified) {
      return ResponseEntity.status(HttpStatus.OK).build();
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

}
