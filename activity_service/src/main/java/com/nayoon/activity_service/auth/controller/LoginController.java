package com.nayoon.activity_service.auth.controller;

import com.nayoon.activity_service.auth.dto.TokenDto;
import com.nayoon.activity_service.auth.dto.request.LoginRequest;
import com.nayoon.activity_service.auth.service.JwtTokenProvider;
import com.nayoon.activity_service.auth.service.LoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LoginController {

  private final LoginService loginService;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 로그인 컨트롤러
   */
  @PostMapping("/login")
  public ResponseEntity<Void> login(
      @Valid @RequestBody LoginRequest request,
      HttpServletResponse response
  ) {
    TokenDto tokenDto = loginService.login(request);
    jwtTokenProvider.accessTokenSetHeader(tokenDto.accessToken(), response);
    jwtTokenProvider.refreshTokenSetHeader(tokenDto.refreshToken(), response);
    return ResponseEntity.ok().build();
  }

}
