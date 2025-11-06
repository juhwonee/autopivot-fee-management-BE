package com.example.capstonedesign20252.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ErrorResponseDto> handleBaseException(BaseException e){
    return ResponseEntity
        .status(e.getStatus())
        .body(new ErrorResponseDto(e.getMessage(), e.getCode(), e.getStatus()));
  }

  // ✅ favicon.ico 등 정적 리소스 요청 시 404 처리
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Void> handleNoResourceFound(NoResourceFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
    log.error("예상치 못한 서버 오류 발생!!", e);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ErrorResponseDto(
            "예상치 못한 서버 오류가 발생했습니다.",
            "UNEXPECTED_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        ));
  }
}
