package com.example.capstonedesign20252.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders; // Base64 디코딩을 위해 추가
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException; // 예외 처리 시 사용
import java.security.Key; // SecretKey 대신 Key 인터페이스 사용
import java.util.Collections; // 권한 부여를 위해 추가
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 인증 객체 생성
import org.springframework.security.core.Authentication; // 인증 인터페이스
import org.springframework.security.core.authority.SimpleGrantedAuthority; // 권한 관리
import org.springframework.security.core.userdetails.User; // Spring Security User 객체
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  // SecretKey 대신 Key 인터페이스 사용
  private final Key secretKey;
  private final long validityInMilliseconds;

  public JwtTokenProvider(
      @Value("${jwt.secret}") String secret,
      @Value("${jwt.expiration}") long validityInMilliseconds) {

    // **수정된 부분: Base64 디코딩을 통해 키 초기화**
    // Base64 문자열을 디코딩하여 안전한 키(Key) 객체 생성 (키 길이 문제 방지)
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    this.validityInMilliseconds = validityInMilliseconds;
  }

  public String createToken(Long userId, String name) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + validityInMilliseconds);

    return Jwts.builder()
               .setSubject(String.valueOf(userId))
               .claim("name", name)
               // Spring Security 권한 정보를 토큰에 추가할 수 있습니다. (예: role=USER)
               // .claim("auth", "ROLE_USER")
               .setIssuedAt(now)
               .setExpiration(validity)
               .signWith(secretKey, SignatureAlgorithm.HS256)
               .compact();
  }

  /**
   * JWT 토큰을 복호화하여 인증 객체(Authentication)를 반환합니다.
   */
  public Authentication getAuthentication(String token) {
    // 1. 토큰에서 클레임 추출
    Claims claims = parseClaims(token);

    // 2. 권한 정보 생성 (현재는 'ROLE_USER'로 고정)
    // 실제 프로젝트에서는 claims.get("auth")와 같은 필드에서 권한 정보를 추출해야 합니다.
    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

    // 3. Spring Security User 객체 생성 (Principal)
    // ID는 Subject(userId), 비밀번호는 사용하지 않으므로 빈 문자열, 권한은 위에서 생성한 권한 사용
    User principal = new User(claims.getSubject(), "", Collections.singleton(grantedAuthority));

    // 4. 인증 객체(Authentication) 반환
    return new UsernamePasswordAuthenticationToken(principal, token, Collections.singleton(grantedAuthority));
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
    } catch (SecurityException e) { // io.jsonwebtoken.security.SecurityException 사용
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
