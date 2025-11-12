package com.example.capstonedesign20252.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResponseDto(
    @JsonProperty("id")
    Long kakaoId,

    @JsonProperty("kakao_account")
    KakaoAccount kakaoAccount
) {
  // 중첩된 record 클래스들
  public record KakaoAccount(
      @JsonProperty("profile")
      Profile profile,

      @JsonProperty("email")
      String email
  ) {
    public record Profile(
        @JsonProperty("nickname")
        String nickname,

        @JsonProperty("profile_image_url")
        String profileImageUrl
    ) {}
  }

  // 편의 메서드들
  public String getNickname() {
    return kakaoAccount != null && kakaoAccount.profile != null
        ? kakaoAccount.profile.nickname
        : null;
  }

  public String getEmail() {
    return kakaoAccount != null ? kakaoAccount.email : null;
  }

  public String getProfileImageUrl() {
    return kakaoAccount != null && kakaoAccount.profile != null
        ? kakaoAccount.profile.profileImageUrl
        : null;
  }
}