package com.nayoon.preordersystem.auth.controller;

import com.nayoon.preordersystem.auth.dto.TokenDto;
import com.nayoon.preordersystem.auth.dto.request.SignInRequest;
import com.nayoon.preordersystem.auth.service.LoginService;
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

  /**
   * 로그인 컨트롤러
   */
  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(
      @Valid @RequestBody SignInRequest request
  ) {
    return ResponseEntity.ok().body(loginService.login(request));
  }

}
