package com.nayoon.newsfeed_service.user.controller;

import com.nayoon.newsfeed_service.auth.security.CustomUserDetails;
import com.nayoon.newsfeed_service.user.dto.request.PasswordUpdateRequest;
import com.nayoon.newsfeed_service.user.dto.request.ProfileUpdateRequest;
import com.nayoon.newsfeed_service.user.dto.request.SignUpRequest;
import com.nayoon.newsfeed_service.user.dto.request.VerifyEmailRequest;
import com.nayoon.newsfeed_service.user.dto.response.UserResponse;
import com.nayoon.newsfeed_service.user.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
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
  public ResponseEntity<String> signup(
      @Valid @RequestPart("data") SignUpRequest request,
      @RequestPart(name = "profileImage") MultipartFile imageFile
  ) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request, imageFile));
  }

  /**
   * 이름, 프로필 이미지, 인사말 업데이트 컨트롤러
   */
  @PatchMapping("/profile")
  public ResponseEntity<UserResponse> updateProfile(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestPart("data") ProfileUpdateRequest request,
      @RequestPart(name = "profileImage", required = false) MultipartFile imageFile
  ) throws IOException {
    return ResponseEntity.ok().body(userService.updateProfile(userDetails, request, imageFile));
  }

  /**
   * 비밀번호 업데이트 컨트롤러
   */
  @PatchMapping("/password")
  public ResponseEntity<Void> updatePassword(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @Valid @RequestBody PasswordUpdateRequest request
  ) {
    userService.updatePassword(userDetails, request);
    return ResponseEntity.ok().build();
  }

  /**
   * 인증 메일 전송 컨트롤러
   */
  @PostMapping("/emails")
  public ResponseEntity<String> sendEmail(
      @Valid @RequestBody String email
  ) {
    return ResponseEntity.ok().body(userService.sendCode(email));
  }

  /**
   * 인증 코드 확인 컨트롤러
   */
  @PostMapping("/emails/verifications")
  public ResponseEntity<Void> verifyEmail(
      @Valid @RequestBody VerifyEmailRequest request
  ) {
    userService.verifyCode(request);
    return ResponseEntity.ok().build();
  }

}