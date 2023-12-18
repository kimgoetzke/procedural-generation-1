package com.hindsight.king_of_castrop_rauxel.web.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
  protected ResponseEntity<Object> handleIllegalArgumentOrState(
      RuntimeException ex, WebRequest request) {
    var body = new ErrorResponse(ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(value = {GenericWebException.class})
  protected ResponseEntity<Object> handleGenericWebException(
      GenericWebException ex, WebRequest request) {
    log.info("GenericWebException: " + ex.getMessage());
    var body = new ErrorResponse(ex.getErrorType(), ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getStatus(), request);
  }

  @ExceptionHandler(value = {InGameException.class})
  protected ResponseEntity<Object> handleInGameException(InGameException ex, WebRequest request) {
    log.debug("InGameException: " + ex.getMessage());
    var body = new ErrorResponse(ex.getErrorType(), ex.getMessage());
    return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getStatus(), request);
  }

  public record ErrorResponse(WebErrorType errorType, String errorMessage) {
    public ErrorResponse(String errorMessage) {
      this(WebErrorType.UNHANDLED, errorMessage);
    }
  }
}
