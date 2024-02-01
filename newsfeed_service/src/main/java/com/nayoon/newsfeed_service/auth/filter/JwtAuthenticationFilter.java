package com.nayoon.newsfeed_service.auth.filter;

import com.nayoon.newsfeed_service.auth.service.JwtTokenProvider;
import com.nayoon.newsfeed_service.common.exception.CustomException;
import com.nayoon.newsfeed_service.common.exception.ErrorCode;
import com.nayoon.newsfeed_service.common.redis.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisService redisService;

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain
  ) throws IOException, ServletException {

    final String token = jwtTokenProvider.resolveAccessToken(request);
    log.info("JwtAuthenticationFilter token: {}", token);

    if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

      boolean isLogout = redisService.keyExists(token);

      if (!isLogout) {
        // 토큰이 유효한 경유 유저 정보 받기
        AbstractAuthenticationToken authentication = jwtTokenProvider.getAuthentication(token);

        // 인증 성공한 경우, SecurityContextHolder 에 인증 객체 설정
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("JWT 인증 필터 통과");
      } else {
        throw new CustomException(ErrorCode.ALREADY_LOGED_OUT);
      }

    }

    chain.doFilter(request, response); // 다음 필터로 이동
  }

}
