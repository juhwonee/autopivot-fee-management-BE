package com.example.capstonedesigin20252.user.dto;

import com.example.capstonedesigin20252.user.domain.User;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record SignUpResponseDto(
    Long userId,
    String name,
    String email,
    LocalDateTime createdAt
) {
  public static SignUpResponseDto from(User user){
    return SignUpResponseDto.builder()
      .userId(user.getId())
      .name(user.getName())
      .email(user.getEmail())
      .createdAt(user.getCreatedAt())
      .build();
  }
}
