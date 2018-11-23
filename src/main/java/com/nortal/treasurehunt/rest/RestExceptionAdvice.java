package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.service.GameTokenService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
  @Resource
  private GameTokenService tokenService;

  @ExceptionHandler(TreasurehuntException.class)
  public ResponseEntity<Error> handle(HttpServletResponse response, TreasurehuntException e) {
    LOG.error("Application error: ", e);
    return handle(response, e.getCode(), e);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Error> handle(HttpServletResponse response, Exception e) {
    LOG.error("Unexpected error: ", e);
    return handle(response, ErrorCode.UNEXPECTED_ERROR, e);
  }

  private ResponseEntity<Error> handle(HttpServletResponse response, ErrorCode code, Exception e) {
    tokenService.writeToken(response);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(code.name(), e.getMessage()));
  }

  private static class Error {
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
