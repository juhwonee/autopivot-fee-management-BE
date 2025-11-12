package com.example.capstonedesign20252.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

  private String clientId;
  private String redirectUri;
  private String tokenUri;
  private String userInfoUri;
  private String frontRedirectUri;
}
