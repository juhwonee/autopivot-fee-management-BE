package com.example.capstonedesign20252.common.exception;

public abstract class BaseException extends RuntimeException {

  public abstract String getMessage();
  public abstract String getCode();
  public abstract int getStatus();

}
