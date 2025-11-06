package com.example.capstonedesign20252.user.dto;

import com.example.capstonedesign20252.user.domain.LoginType;
import com.example.capstonedesign20252.user.domain.User;

public record UserInfoResponseDto(
    Long userId,
    String name,
    String email,
    LoginType loginType
) {
  public static UserInfoResponseDto from(User user){
    return new UserInfoResponseDto(
        user.getId(),
        user.getName(),
        user.getEmail(),
        user.getLoginType()
    );
  }
}
