package com.example.capstonedesign20252.auth.exception;

import lombok.Getter;

@Getter
public class KakaoException extends RuntimeException {
  private final KakaoErrorCode errorCode;

  public KakaoException(KakaoErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public KakaoException(KakaoErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }
}
