package com.nayoon.preordersystem.auth.filter;

import com.nayoon.preordersystem.auth.service.JwtTokenProvider;
import com.nayoon.preordersystem.common.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain
  ) throws IOException, ServletException {
    final String token = jwtTokenProvider.extractTokenFromRequest(request);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
      String isLogout = (String) redisService.getValue(token);

      if (ObjectUtils.isEmpty(isLogout)) {
        // 토큰이 유효한 경유 유저 정보 받기
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // 인증 성공한 경우, SecurityContextHolder 에 인증 객체 설정
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    chain.doFilter(request, response); // 다음 필터로 이동
  }

}
