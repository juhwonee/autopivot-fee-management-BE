package com.example.capstonedesign20252.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final SecretKey secretKey;
  private final long validityInMilliseconds;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration}") long validityInMilliseconds) {
    this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.validityInMilliseconds = validityInMilliseconds;
  }

  public String createToken(Long userId, String name) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
        .setSubject(String.valueOf(userId))
        .claim("name", name)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(secretKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public Long getUserId(String token){
    Claims claims = parseClaims(token);
    return Long.parseLong(claims.getSubject());
  }

  public String getUserName(String token) {
    Claims claims = parseClaims(token);
    return claims.get("name", String.class);
  }

  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.warn("지원되지 않는 JWT 토큰입니다.");
    } catch (MalformedJwtException e) {
      log.warn("잘못된 형식의 JWT 토큰입니다.");
    } catch (SecurityException e) {
      log.warn("JWT 서명 검증에 실패했습니다.");
    } catch (IllegalArgumentException e) {
      log.warn("JWT 토큰이 비어있습니다.");
    }
    return false;
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(secretKey)
               .build()
               .parseClaimsJws(token)
               .getBody();
  }
}
