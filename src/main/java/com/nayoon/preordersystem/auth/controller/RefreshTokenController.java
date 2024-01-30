package com.nayoon.preordersystem.auth.controller;

import com.nayoon.preordersystem.auth.service.JwtTokenProvider;
import com.nayoon.preordersystem.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/refreshToken")
@RequiredArgsConstructor
public class RefreshTokenController {

  private final RefreshTokenService refreshTokenService;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * accessToken 재발급
   */
  @PostMapping
  public ResponseEntity<Void> refresh(
      HttpServletRequest request,
      HttpServletResponse response
  ) {
    String accessToken = refreshTokenService.refresh(request);
    jwtTokenProvider.accessTokenSetHeader(accessToken, response);
    return ResponseEntity.ok().build();
  }

}
