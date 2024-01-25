package com.nayoon.preordersystem.auth.security;

import com.nayoon.preordersystem.auth.dto.TokenDto;
import com.nayoon.preordersystem.common.exception.CustomException;
import com.nayoon.preordersystem.common.exception.ErrorCode;
import com.nayoon.preordersystem.user.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  private final CustomUserDetailsService customUserDetailsService;

  @Getter
  private static long accessTokenValidationTime ; // accessToken 만료시간
  private static long refreshTokenValidationTime ; // refreshToken 만료시간
  private static SecretKey key; // secretKey를 Key 객체로 해싱

  public JwtTokenProvider(
      CustomUserDetailsService customUserDetailsService, @Value("${spring.jwt.secret}") final String secretKey,
      @Value("${spring.jwt.access-token-valid-time}") final long accessTokenValidationTime,
      @Value("${spring.jwt.refresh-token-valid-time}") final long refreshTokenValidationTime
  ) {
    this.customUserDetailsService = customUserDetailsService;
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidationTime  = accessTokenValidationTime;
    this.refreshTokenValidationTime  = refreshTokenValidationTime;
  }

  /**
   * 토큰 생성 메서드
   */
  public TokenDto generateToken(String email, String name, UserRole role) {
    Date now = new Date();

    // accessToken 생성
    String accessToken = Jwts.builder()
        .setSubject(email)
        .claim("name", name)
        .claim("email", email)
        .claim("role", role.getName())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + accessTokenValidationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    // refreshToken 생성
    String refreshToken = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshTokenValidationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return new TokenDto(accessToken, refreshToken, now.getTime() + refreshTokenValidationTime);
  }

  /**
   * 인증된 유저인지 확인
   */
  public Authentication getAuthentication(String token) {
    CustomUserDetails principalDetails =
        (CustomUserDetails) customUserDetailsService.loadUserByUsername(this.getEmail(token));
    return new UsernamePasswordAuthenticationToken(principalDetails, "",
        principalDetails.getAuthorities());
  }

  /**
   * 요청에서 토큰 반환하는 메서드
   */
  public String extractTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring("Bearer ".length()); // "Bearer " 부분을 제외한 토큰 값 반환
    }
    return null;
  }

  /**
   * 토큰에서 아이디 추출하는 메서드
   */
  public String getEmail(String token) {
    if (!validateToken(token)) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }

    return extractClaims(token).get("email", String.class);
  }

  /**
   * 토큰이 유효한지 확인하는 메서드
   */
  public boolean validateToken(String token) {
    try {

      Claims claims = extractClaims(token);
      return claims.getExpiration().after(new Date());

    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_AUTHENTICATION_TOKEN);
    }
  }

  /**
   * 토큰에러 Claims 추출하는 메서드
   */
  private Claims extractClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token)
        .getBody();
  }

  // accessToken 유효 시간 반환
  public Long getAccessTokenExpiration(String accessToken) {
    Claims claims = extractClaims(accessToken);
    Date expiration = claims.getExpiration();
    Date now = new Date();

    return expiration.getTime() - now.getTime();
  }

}
