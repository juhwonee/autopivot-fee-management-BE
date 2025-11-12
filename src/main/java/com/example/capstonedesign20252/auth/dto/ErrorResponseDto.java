package com.example.capstonedesign20252.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class ErrorResponseDto {
  private final String message;
  private final int status;

  public ErrorResponseDto(String message, int status) {
    this.message = message;
    this.status = status;
  }
}
