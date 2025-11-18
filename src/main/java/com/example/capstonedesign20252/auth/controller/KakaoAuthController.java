package com.example.capstonedesign20252.auth.controller;


import com.example.capstonedesign20252.auth.config.KakaoProperties;
import com.example.capstonedesign20252.auth.service.KakaoAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KakaoAuthController {

  private final KakaoProperties kakaoProperties;
  private final KakaoAuthService kakaoAuthService;

  @GetMapping("/kakao-login")
  public RedirectView getAuthKakao(){
    String kakaoAuthUrl =  "https://kauth.kakao.com/oauth/authorize?"
        + "client_id=" + kakaoProperties.getClientId()
        + "&redirect_uri=" + kakaoProperties.getRedirectUri()
        + "&response_type=code";

    return new RedirectView(kakaoAuthUrl);
  }

  @GetMapping("/kakao-callback")
  public RedirectView KakaoCallback(@RequestParam String code){
    log.info("카카오 인가코드 수신 완료");
    log.info(code);

    try{
      String jwtToken = kakaoAuthService.loginWithKakao(code);

      String redirectUrl = kakaoProperties.getFrontRedirectUri() + "?token=" + jwtToken;
      log.info("프론트엔드로 리다이렉트 - 로그인 성공");

      return new RedirectView(redirectUrl);
    } catch (Exception e) {
      log.error("카카오 로그인 실패", e);
      String errorUrl = kakaoProperties.getFrontRedirectUri() + "?error=login_fail&message=" + e.getMessage();

      return new RedirectView(errorUrl);
    }
  }
}
