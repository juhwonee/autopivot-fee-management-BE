package com.example.capstonedesigin20252.user.dto;

import com.example.capstonedesigin20252.user.domain.User;
import lombok.Builder;

@Builder
public record LoginResponseDto(
    Long userId,
    String name,
    String email
) {
  public static LoginResponseDto from(User user){
    return LoginResponseDto.builder()
      .userId(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .build();
  }
}
