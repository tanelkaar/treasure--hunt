package com.nortal.treasurehunt.util;

/**
 * @author Lauri Lättemäe <lauri.lattemae@nortal.com>
 */
public class IDUtil {
  private static Long ID = 1L;

  private IDUtil() {
  }

  public static Long getNext() {
    return ID++;
  }
}
