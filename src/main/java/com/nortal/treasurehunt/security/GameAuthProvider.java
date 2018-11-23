package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.service.GameService;
import javax.annotation.Resource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Deprecated
public class GameAuthProvider implements AuthenticationProvider {
  @Resource
  private GameService gameService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    GameToken authData = ((GameAuth) authentication).getDetails();
    // authentication.setAuthenticated(gameService.isValid(authData));
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return GameAuth.class.isAssignableFrom(authentication);
  }
}
