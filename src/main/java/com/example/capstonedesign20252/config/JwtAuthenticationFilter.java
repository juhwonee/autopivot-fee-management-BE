package com.example.capstonedesign20252.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 1. Request Header에서 JWT 토큰 추출
    String jwt = resolveToken(request);

    // 2. validateToken으로 토큰 유효성 검사
    if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

      // 3. 토큰이 유효할 경우, 토큰으로부터 Authentication 객체를 받아옴
      Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

      // 4. SecurityContext에 Authentication 객체를 저장
      // 이로 인해 @AuthenticationPrincipal 등을 통해 사용자 정보에 접근 가능
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
    } else {
      // 토큰이 없거나 유효하지 않은 경우
      log.debug("유효한 JWT 토큰이 없습니다. uri: {}", request.getRequestURI());
    }

    // 다음 필터로 진행
    filterChain.doFilter(request, response);
  }

  /**
   * Request Header에서 토큰 정보 추출
   * @param request HTTP 요청 객체
   * @return JWT 토큰 문자열
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
      // "Bearer " 접두사를 제외한 실제 JWT 값만 반환
      return bearerToken.substring(BEARER_PREFIX.length());
    }
    return null;
  }
}