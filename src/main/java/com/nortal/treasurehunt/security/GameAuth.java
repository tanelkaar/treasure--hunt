package com.nortal.treasurehunt.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class GameAuth extends AbstractAuthenticationToken {
  public GameAuth(GameAuthData authData) {
    super(null);
    super.setDetails(authData);
    super.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }

  @Override
  public GameAuthData getDetails() {
    return (GameAuthData) super.getDetails();
  }
}
