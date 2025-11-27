package com.example.capstonedesign20252.group.domain;

import com.example.capstonedesign20252.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {

  GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP-001", "해당 그룹이 존재하지 않습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
