package com.nortal.treasurehunt.util;

public class IDUtil {
  private static Long ID = 1L;

  private IDUtil() {
  }

  public static Long getNext() {
    return ID++;
  }
}
