package com.example.capstonedesign20252.config;

import com.example.capstonedesign20252.auth.jwt.JwtAuthenticationFilter;
import com.example.capstonedesign20252.auth.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  @Bean
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);

    // IMPORTANT: 백엔드 주소로 프록시되는 Vercel의 프론트엔드 도메인을 추가해야 합니다.
    config.setAllowedOriginPatterns(List.of(
        "https://autopivot-fee-management-fe.vercel.app",
        "http://localhost:3000",
        "http://localhost:5173"
        // TODO: 프론트엔드의 최종 Vercel 배포 주소를 정확하게 확인하여 추가하세요.
    ));

    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
    config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 1. CORS 설정 적용
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

        // 2. JWT 사용 시 CSRF는 비활성화
        .csrf(AbstractHttpConfigurer::disable)

        // 3. 세션 사용 안 함 (STATELESS)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // 4. 요청별 접근 권한 설정 (핵심 수정)
        .authorizeHttpRequests(auth -> auth
            // 로그인, 회원가입, 소셜 로그인 등 인증/인가 없이 허용할 경로
            .requestMatchers("/auth/**", "/login/**", "/api/auth/**").permitAll()

            // 그 외 나머지 모든 요청은 반드시 인증 필요
            .anyRequest().authenticated()
        )

        // 5. JWT 토큰 검증 필터 추가 (핵심 추가)
        // Spring Security의 기본 인증 필터 이전에 JWT 필터를 실행하여 토큰 유효성 검사
        .addFilterBefore(
            new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class
        );

    return http.build();
  }
}