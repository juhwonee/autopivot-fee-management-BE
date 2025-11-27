package com.example.capstonedesign20252.groupMember.domain;

import com.example.capstonedesign20252.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupMemberErrorCode implements ErrorCode {

  NOT_GROUP_ADMIN(HttpStatus.FORBIDDEN, "MEMBER-001", "그룹의 관리자만 이 작업을 수행할 수 있습니다."),
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-002", "해당 그룹 멤버를 찾을 수 없습니다."),
  NOT_DELETE_ADMIN(HttpStatus.FORBIDDEN, "MEMBER-003", "그룹 관리자는 삭제할 수 없습니다."),
  DUPLICATE_GROUP_MEMBER(HttpStatus.CONFLICT, "MEMBER-004", "이미 존재하는 멤버입니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
