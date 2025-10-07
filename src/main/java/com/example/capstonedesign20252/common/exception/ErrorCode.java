package com.example.capstonedesign20252.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  String getMessage();
  String getCode();
  HttpStatus getStatus();
}
