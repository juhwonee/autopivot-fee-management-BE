package com.example.capstonedesign20252.group.domain;


import com.example.capstonedesign20252.common.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupException extends BaseException {

  private final GroupErrorCode groupErrorCode;

  @Override
  public String getMessage() {
    return groupErrorCode.getMessage();
  }

  @Override
  public String getCode() {
    return groupErrorCode.getCode();
  }

  @Override
  public int getStatus() {
    return groupErrorCode.getStatus().value();
  }
}
