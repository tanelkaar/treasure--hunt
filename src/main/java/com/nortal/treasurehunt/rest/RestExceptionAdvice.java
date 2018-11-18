package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionAdvice extends ResponseEntityExceptionHandler {
  private static final Logger LOG = LoggerFactory.getLogger(RestExceptionAdvice.class);

  @ExceptionHandler(TreasurehuntException.class)
  public ResponseEntity<Error> handle(TreasurehuntException e) {
    LOG.error("Application error: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(e.getCode().name(), e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handle(Exception e) {
    LOG.error("Unexpected error: ", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new Error(ErrorCode.UNEXPECTED_ERROR.name(), e.getMessage()));
  }

  private class Error {
    private String errorCode;
    private String description;

    public Error(String code, String description) {
      this.errorCode = code;
      this.description = description;
    }

    public String getErrorCode() {
      return errorCode;
    }

    public String getDescription() {
      return description;
    }
  }
}
