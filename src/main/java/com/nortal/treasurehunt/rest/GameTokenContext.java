package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.model.GameToken;

public class GameTokenContext {
  private static ThreadLocal<GameToken> tokenStore = new ThreadLocal<>();

  public static GameToken get() {
    return tokenStore.get();
  }

  public static void set(GameToken token) {
    tokenStore.set(token);
  }
}
