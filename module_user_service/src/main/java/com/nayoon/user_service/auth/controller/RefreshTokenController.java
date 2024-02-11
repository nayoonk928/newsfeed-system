package com.nayoon.user_service.auth.controller;

import com.nayoon.user_service.auth.service.JwtTokenProvider;
import com.nayoon.user_service.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * accessToken 재발급
   */
  @PostMapping("/refreshToken")
  public ResponseEntity<Void> refresh(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    String accessToken = refreshTokenService.refresh(request);
    jwtTokenProvider.accessTokenSetHeader(accessToken, response);
    return ResponseEntity.ok().build();
  }

}
