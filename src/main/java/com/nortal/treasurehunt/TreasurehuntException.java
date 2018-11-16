package com.nortal.treasurehunt;

import com.nortal.treasurehunt.enums.ErrorCode;

public class TreasurehuntException extends RuntimeException {
  private ErrorCode code;

  public TreasurehuntException(ErrorCode code) {
    this(code, null);
  }

  public TreasurehuntException(ErrorCode code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCode getCode() {
    return code;
  }
}
