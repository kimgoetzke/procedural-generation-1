package com.hindsight.king_of_castrop_rauxel.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InGameException extends RuntimeException {

  private final HttpStatus status;
  private static final WebErrorType errorType = WebErrorType.IN_GAME;

  public InGameException(String s) {
    super(s);
    status = HttpStatus.BAD_REQUEST;
  }

  public WebErrorType getErrorType() {
    return errorType;
  }
}
