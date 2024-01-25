package com.nayoon.preordersystem.auth.filter;

import com.nayoon.preordersystem.auth.security.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain
  ) throws IOException, ServletException {
    final String token = jwtTokenProvider.extractTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      // 토큰이 유효한 경유 유저 정보 받기
      Authentication authentication = jwtTokenProvider.getAuthentication(token);
      // 인증 성공한 경우, SecurityContextHolder 에 인증 객체 설정
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response); // 다음 필터로 이동
  }

}
