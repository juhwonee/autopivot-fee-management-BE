package com.example.capstonedesign20252.auth.service;

import com.example.capstonedesign20252.auth.client.KakaoAuthClient;
import com.example.capstonedesign20252.auth.dto.KakaoTokenResponseDto;
import com.example.capstonedesign20252.auth.dto.KakaoUserInfoResponseDto;
import com.example.capstonedesign20252.auth.jwt.JwtTokenProvider;
import com.example.capstonedesign20252.user.domain.User;
import com.example.capstonedesign20252.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class KakaoAuthService {

  private final KakaoAuthClient kakaoAuthClient;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public String loginWithKakao(String code){
    log.info("카카오 로그인 시작");

    KakaoTokenResponseDto tokenResponseDto = kakaoAuthClient.requestAccessToken(code);
    log.info("카카오 액세스 토큰 발급 성공");

    KakaoUserInfoResponseDto userInfo = kakaoAuthClient.requestKakaoUserInfo(tokenResponseDto.accessToken());
    String kakaoId = String.valueOf(userInfo.kakaoId());
    log.info("카카오 사용자 정보 조회 성공 - kakaoId: {}", kakaoId);

    User user = findOrCreateKakaoUser(userInfo);
    log.info("사용자 처리 완료 - userId: {}, name: {}", user.getId(), user.getName());

    String jwtToken = jwtTokenProvider.createToken(user.getId(), user.getName());
    log.info("JWT 토큰 발급 완료");

    return jwtToken;
  }

  private User findOrCreateKakaoUser(KakaoUserInfoResponseDto userInfo) {
    String kakaoId = String.valueOf(userInfo.kakaoId());

    Optional<User> existingUser = userRepository.findByKakaoId(kakaoId);

    if (existingUser.isPresent()) {
      log.info("이미 가입된 카카오 유저입니다.");
      return existingUser.get();
    }

    String nickname = userInfo.getNickname();
    String email = userInfo.getEmail();
    String profileImageUrl = userInfo.getProfileImageUrl();

    User newUser = User.createKakaoUser(nickname, email, kakaoId, profileImageUrl);
    log.info("새로운 카카오 유저 가입 완료");

    return userRepository.save(newUser);
  }
}
