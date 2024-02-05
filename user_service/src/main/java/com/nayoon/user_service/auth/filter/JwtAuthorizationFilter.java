package com.nayoon.user_service.auth.filter;

import com.nayoon.user_service.auth.security.CustomUserDetailsService;
import com.nayoon.user_service.auth.service.JwtTokenProvider;
import com.nayoon.user_service.common.exception.CustomException;
import com.nayoon.user_service.common.exception.ErrorCode;
import com.nayoon.user_service.common.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final CustomUserDetailsService customUserDetailsService;
  private final RedisService redisService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String token = jwtTokenProvider.resolveAccessToken(request);
    log.info("JwtAuthorizationFilter token: {}", token);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

      boolean isLogout = redisService.keyExists(token);

      if (!isLogout) {
        String username = jwtTokenProvider.getEmail(token);
        setAuthentication(username);
        log.info("JWT 인증 필터 통과");
      } else {
        throw new CustomException(ErrorCode.ALREADY_LOGED_OUT);
      }

    }

    filterChain.doFilter(request, response); // 다음 필터로 이동
  }

  // 인증 처리
  public void setAuthentication(String username) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    Authentication authentication = createAuthentication(username);
    context.setAuthentication(authentication);

    SecurityContextHolder.setContext(context);
  }

  // 인증 객체 생성
  private Authentication createAuthentication(String username) {
    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
    return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
  }

}
