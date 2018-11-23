package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.model.GameToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Deprecated
public class GameAuth extends AbstractAuthenticationToken {
  public GameAuth(GameToken authData) {
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
  public GameToken getDetails() {
    return (GameToken) super.getDetails();
  }
}
