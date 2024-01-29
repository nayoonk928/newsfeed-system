package com.nayoon.preordersystem.auth.service;

import com.nayoon.preordersystem.auth.security.CustomUserDetails;
import com.nayoon.preordersystem.auth.security.CustomUserDetailsService;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class JwtTokenProvider {

  private final CustomUserDetailsService customUserDetailsService;

  @Getter
  private static long accessTokenValidationTime ; // accessToken 만료시간
  private static long refreshTokenValidationTime ; // refreshToken 만료시간
  private static SecretKey key; // secretKey를 Key 객체로 해싱

  public JwtTokenProvider(
      CustomUserDetailsService customUserDetailsService,
      @Value("${spring.jwt.secret}") final String secretKey,
      @Value("${spring.jwt.access-token-valid-time}") final long accessTokenValidationTime,
      @Value("${spring.jwt.refresh-token-valid-time}") final long refreshTokenValidationTime
  ) {
    this.customUserDetailsService = customUserDetailsService;
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidationTime  = accessTokenValidationTime;
    this.refreshTokenValidationTime  = refreshTokenValidationTime;
  }

  /**
   * AccessToken 생성 메서드
   */
  public String generateAccessToken(User user) {
    Date now = new Date();
    String email = user.getEmail();
    String role = user.getUserRole().getName();

    Claims claims = Jwts.claims();
    claims.put("email", email);
    claims.put("role", role);

    // 토큰 생성
    String accessToken = Jwts.builder()
        .setSubject(email)
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + accessTokenValidationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return accessToken;
  }

  /**
   * RefreshToken 생성 메서드
   */
  public String generateRefreshToken(User user) {
    Date now = new Date();

    // 토큰 생성
    String accessToken = Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshTokenValidationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return accessToken;
  }

  /**
   * 인증된 유저인지 확인
   */
  public AbstractAuthenticationToken getAuthentication(String token) {
    log.info("JwtTokenProvider getAuthentication email: {}", getEmail(token));
    CustomUserDetails userDetails =
        (CustomUserDetails) customUserDetailsService.loadUserByUsername(getEmail(token));
    return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
  }

  /**
   * 토큰에서 이메일 추출하는 메서드
   */
  public String getEmail(String token) {
    if (!validateToken(token)) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }

    return parseClaims(token).getSubject();
  }

  /**
   * 토큰이 유효한지 확인하는 메서드
   */
  public boolean validateToken(String token) {
    try {

      Claims claims = parseClaims(token);
      return claims.getExpiration().after(new Date());

    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }
  }

  /**
   * 토큰 유효시간 반환
   */
  public Long getExpiredTime(String token) {
    Claims claims = parseClaims(token);
    Date expiration = claims.getExpiration();
    Date now = new Date();

    return expiration.getTime() - now.getTime();
  }

  /**
   * 토큰에서 Claims 추출하는 메서드
   */
  public Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token)
        .getBody();
  }

  public void accessTokenSetHeader(String accessToken, HttpServletResponse response) {
    String headerValue = "Bearer " + accessToken;
    response.setHeader("Authorization", headerValue);
  }

  public void refreshTokenSetHeader(String refreshToken, HttpServletResponse response) {
    response.setHeader("Refresh", refreshToken);
  }

  /**
   * 요청에서 AccessToken 반환하는 메서드
   */
  public String resolveAccessToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring("Bearer ".length()); // "Bearer " 부분을 제외한 토큰 값 반환
    }
    return null;
  }

  /**
   * 요청에서 RefreshToken 반환하는 메서드
   */
  public String resolveRefreshToken(HttpServletRequest request) {
    String header = request.getHeader("Refresh");
    if (StringUtils.hasText(header)) {
      return header;
    }
    return null;
  }

}
