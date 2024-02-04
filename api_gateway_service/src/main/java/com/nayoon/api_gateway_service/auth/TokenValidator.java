package com.nayoon.api_gateway_service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenValidator {

  private final String secretKey;

  private static SecretKey key; // secretKey를 Key 객체로 해싱

  @Autowired
  public TokenValidator(@Value("${spring.jwt.secret}") String secretKey) {
    this.secretKey = secretKey;
  }

  @PostConstruct
  protected void init() {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * 토큰이 유효한지 확인하는 메서드
   */
  public static boolean validateToken(String token) {
    Claims claims = parseClaims(token);
    return claims.getExpiration().after(new Date());
  }

  /**
   * 토큰에서 Claims 추출하는 메서드
   */
  private static Claims parseClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token)
        .getBody();
  }

}
