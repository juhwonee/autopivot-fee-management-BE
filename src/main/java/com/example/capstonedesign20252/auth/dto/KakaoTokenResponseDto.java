package com.example.capstonedesign20252.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponseDto(
    @JsonProperty("access_token")
    String accessToken,

    @JsonProperty("expires_in")
    int expiresIn
) {
}
