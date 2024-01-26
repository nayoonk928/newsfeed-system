package com.nayoon.preordersystem.auth.controller;

import com.nayoon.preordersystem.auth.dto.request.LogoutRequest;
import com.nayoon.preordersystem.auth.security.CustomUserDetails;
import com.nayoon.preordersystem.auth.service.LogoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LogoutController {

  private final LogoutService logoutService;

  /**
   * 로그아웃 컨트롤러
   */
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @AuthenticationPrincipal CustomUserDetails user,
      @Valid @RequestBody LogoutRequest request
  ) {
    logoutService.logout(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

}
