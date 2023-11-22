package com.hindsight.king_of_castrop_rauxel.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {IllegalArgumentException.class, IllegalStateException.class})
  protected ResponseEntity<Object> handleIllegalArgumentOrState(
      RuntimeException ex, WebRequest request) {
    var bodyOfResponse = ex.getMessage();
    return handleExceptionInternal(
        ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
