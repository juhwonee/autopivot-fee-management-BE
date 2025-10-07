package com.example.capstonedesign20252.user.exception;

import com.example.capstonedesign20252.common.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends BaseException {

  private final UserErrorCode userErrorCode;

  @Override
  public String getMessage() {
    return userErrorCode.getMessage();
  }

  @Override
  public String getCode() {
    return userErrorCode.getCode();
  }

  @Override
  public int getStatus() {
    return userErrorCode.getStatus().value();
  }
}
