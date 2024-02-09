package com.nayoon.user_service.auth.service;

import com.nayoon.user_service.auth.security.CustomUserDetailsService;
import com.nayoon.user_service.common.exception.CustomException;
import com.nayoon.user_service.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String REFRESH_HEADER = "Refresh";
  public static final String BEARER_PREFIX = "Bearer ";

  private final CustomUserDetailsService customUserDetailsService;

  @Getter
  @Value("${spring.jwt.access-token-valid-time}")
  private long accessTokenValidationTime ; // accessToken 만료시간

  @Value("${spring.jwt.refresh-token-valid-time}")
  private long refreshTokenValidationTime ; // refreshToken 만료시간

  @Value("${spring.jwt.secret}")
  private String secretKey;
  private SecretKey key; // secretKey를 Key 객체로 해싱

  @PostConstruct
  protected void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * AccessToken 생성 메서드
   */
  public String generateAccessToken(String email, Long userId) {
    Date now = new Date();
    return createToken(now, email, userId, accessTokenValidationTime);
  }

  /**
   * RefreshToken 생성 메서드
   */
  public String generateRefreshToken(String email, Long userId) {
    Date now = new Date();
    return createToken(now, email, userId, refreshTokenValidationTime);
  }

  private String createToken(Date now, String email, Long userId, long validationTime) {
    String token = Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("email", email)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + validationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return token;
  }

  /**
   * 토큰에서 이메일 추출하는 메서드
   */
  public String getEmail(String token) {
    if (!validateToken(token)) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }

    return parseClaims(token).get("email").toString();
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
    String headerValue = BEARER_PREFIX + accessToken;
    response.setHeader(AUTHORIZATION_HEADER, headerValue);
  }

  public void refreshTokenSetHeader(String refreshToken, HttpServletResponse response) {
    response.setHeader(REFRESH_HEADER, refreshToken);
  }

  /**
   * 요청에서 AccessToken 반환하는 메서드
   */
  public String resolveAccessToken(HttpServletRequest request) {
    String header = request.getHeader(AUTHORIZATION_HEADER);
    if (header != null && header.startsWith(BEARER_PREFIX)) {
      return header.substring(BEARER_PREFIX.length()); // "Bearer " 부분을 제외한 토큰 값 반환
    }
    return null;
  }

  /**
   * 요청에서 RefreshToken 반환하는 메서드
   */
  public String resolveRefreshToken(HttpServletRequest request) {
    String header = request.getHeader(REFRESH_HEADER);
    if (StringUtils.hasText(header)) {
      return header;
    }
    return null;
  }

}