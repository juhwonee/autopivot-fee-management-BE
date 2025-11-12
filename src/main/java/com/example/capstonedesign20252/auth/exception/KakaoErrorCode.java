package com.example.capstonedesign20252.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KakaoErrorCode {

  AUTH_CODE_INVALID("KAKAO_001","유효하지 않은 인가 코드입니다.", 400),
  AUTH_CODE_EXPIRED("KAKAO_002","인가 코드가 만료되었습니다.", 400),
  AUTH_CODE_ALREADY_USED("KAKAO_003","이미 사용된 인가 코드입니다.", 400),
  REDIRECT_URI_MISMATCH("KAKAO_004","Redirect URI가 일치하지 않습니다.", 400),
  INVALID_CLIENT_ID("KAKAO_005","유효하지 않은 Client ID입니다.", 400),
  TOKEN_REQUEST_FAILED_CLIENT("KAKAO_006","토큰 요청 실패 (클라이언트 오류)", 400),
  TOKEN_REQUEST_FAILED_SERVER("KAKAO_007","토큰 요청 실패 (서버 오류)", 500),
  TOKEN_REQUEST_FAILED_NULL_BODY("KAKAO_008","토큰 응답이 비어있습니다.", 500),
  TOKEN_REQUEST_FAILED_NO_TOKEN("KAKAO_009","토큰 응답에 access_token이 없습니다.", 500),

  // 사용자 정보 관련 에러
  USER_INFO_REQUEST_FAILED("KAKAO_010","사용자 정보 조회 실패", 400),

  // 네트워크 에러
  CONNECTION_FAILED("KAKAO_011","카카오 서버 연결 실패", 503),

  // 기타
  DUPLICATE_USER_EXCEPTION("KAKAO_012", "이미 가입된 카카오 유저입니다.", 409),
  UNKNOWN_ERROR("KAKAO_013","알 수 없는 오류가 발생했습니다.", 500);

  private final String errorCode;
  private final String message;
  private final int status;
}
