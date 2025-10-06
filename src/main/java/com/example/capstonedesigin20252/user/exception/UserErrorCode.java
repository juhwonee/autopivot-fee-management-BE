package com.example.capstonedesigin20252.user.exception;

import com.example.capstonedesigin20252.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "해당 회원이 존재하지 않습니다."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER-002", "비밀번호가 일치하지 않습니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER-003", "이미 가입된 이메일입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
