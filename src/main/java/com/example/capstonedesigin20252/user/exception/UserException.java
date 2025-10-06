package com.example.capstonedesigin20252.user.exception;

import com.example.capstonedesigin20252.common.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;

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
