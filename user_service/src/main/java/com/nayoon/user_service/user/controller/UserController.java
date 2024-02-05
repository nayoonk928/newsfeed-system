package com.nayoon.user_service.user.controller;

import com.nayoon.user_service.user.dto.request.PasswordUpdateRequest;
import com.nayoon.user_service.user.dto.request.ProfileUpdateRequest;
import com.nayoon.user_service.user.dto.request.SignUpRequest;
import com.nayoon.user_service.user.dto.response.UserResponse;
import com.nayoon.user_service.user.dto.response.UserSignUpResponse;
import com.nayoon.user_service.user.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

  private final UserService userService;

  /**
   * 회원가입 컨트롤러
   */
  @PostMapping("/signup")
  public ResponseEntity<UserSignUpResponse> signup(
      @Valid @RequestPart("data") SignUpRequest request,
      @RequestPart(name = "profileImage") MultipartFile imageFile
  ) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request, imageFile));
  }

  /**
   * 이름, 프로필 이미지, 인사말 업데이트 컨트롤러
   */
  @PatchMapping("/users/profile")
  public ResponseEntity<UserResponse> updateProfile(
      @RequestHeader("X-USER-ID") String principalId,
      @Valid @RequestPart("data") ProfileUpdateRequest request,
      @RequestPart(name = "profileImage", required = false) MultipartFile imageFile
  ) throws IOException {
    log.info("User profile update Start");
    return ResponseEntity.ok().body(userService.updateProfile(Long.valueOf(principalId), request, imageFile));
  }

  /**
   * 비밀번호 업데이트 컨트롤러
   */
  @PatchMapping("/users/password")
  public ResponseEntity<Void> updatePassword(
      @RequestHeader("X-USER-ID") String principalId,
      @Valid @RequestBody PasswordUpdateRequest request
  ) {
    userService.updatePassword(Long.valueOf(principalId), request);
    return ResponseEntity.ok().build();
  }

}
