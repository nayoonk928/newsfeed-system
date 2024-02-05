package com.nayoon.user_service.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayoon.user_service.auth.dto.TokenDto;
import com.nayoon.user_service.auth.dto.request.LoginRequest;
import com.nayoon.user_service.auth.service.JwtTokenProvider;
import com.nayoon.user_service.auth.service.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final LoginService loginService;

  private LoginRequest requestDto;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, LoginService loginService) {
    this.jwtTokenProvider = jwtTokenProvider;
    this.loginService = loginService;
    setFilterProcessesUrl("/api/v1/auth/login");
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {
    log.info("JwtAuthenticationFilter Start");
    try {
      requestDto = new ObjectMapper().readValue(request.getInputStream(),
          LoginRequest.class);

      return getAuthenticationManager().authenticate(
          new UsernamePasswordAuthenticationToken(
              requestDto.email(),
              requestDto.password(),
              null
          )
      );
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) {
    TokenDto tokenDto = loginService.login(requestDto);
    jwtTokenProvider.accessTokenSetHeader(tokenDto.accessToken(), response);
    jwtTokenProvider.refreshTokenSetHeader(tokenDto.refreshToken(), response);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, AuthenticationException failed) {
    response.setStatus(401);
  }

}
