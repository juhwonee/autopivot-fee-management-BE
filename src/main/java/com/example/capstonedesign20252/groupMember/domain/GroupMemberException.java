package com.example.capstonedesign20252.groupMember.domain;

import com.example.capstonedesign20252.common.exception.BaseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GroupMemberException extends BaseException {

  private final GroupMemberErrorCode groupMemberErrorCode;

  @Override
  public String getMessage() {
    return groupMemberErrorCode.getMessage();
  }

  @Override
  public String getCode() {
    return groupMemberErrorCode.getCode();
  }

  @Override
  public int getStatus() {
    return groupMemberErrorCode.getStatus().value();
  }
}
