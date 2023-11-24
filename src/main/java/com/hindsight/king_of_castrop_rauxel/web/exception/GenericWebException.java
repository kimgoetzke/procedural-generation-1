package com.hindsight.king_of_castrop_rauxel.web.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GenericWebException extends RuntimeException {

  private final HttpStatus status;

  public GenericWebException(String s) {
    super(s);
    status = HttpStatus.BAD_REQUEST;
  }

  public GenericWebException(String s, HttpStatus status) {
    super(s);
    this.status = status;
  }
}
