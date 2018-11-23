package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.service.GameService;
import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

@Deprecated
public class GameAuthFilter extends AbstractAuthenticationProcessingFilter {
  private static final Logger LOG = LoggerFactory.getLogger(GameAuthFilter.class);
  public static final String GAME_TOKEN = "game-token";

  @Resource
  private GameService gameService;

  public GameAuthFilter(String path) {
    super(path);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    Authentication auth = authenticate(getAuthCookie(request));
    // missing or invalid cookie - create member
    if (auth == null) {
      // auth =
      // getAuthenticationManager().authenticate(new GameAuth(new
      // GameAuthData(gameService.createMember().getId())));
    }

    if (!auth.isAuthenticated()) {
      throw new BadCredentialsException("Unable to authenticate!");
    }
    return auth;
  }

  public Authentication authenticate(Cookie authCookie) {
    GameToken authData = readAndVerify(authCookie);
    if (authData == null) {
      return null;
    }
    Authentication auth = getAuthenticationManager().authenticate(new GameAuth(authData));
    return auth.isAuthenticated() ? auth : null;
  }

  public GameToken readAndVerify(Cookie authCookie) {
    if (authCookie == null) {
      return null;
    }

    String gameToken = authCookie.getValue();
    try {
      // return gameService.getAuthData(gameToken);
    } catch (Exception e) {
      LOG.error(String.format("Unable to verify authToken=%s", gameToken), e);
    }
    return null;
  }

  private Cookie getAuthCookie(HttpServletRequest request) {
    if (ArrayUtils.isEmpty(request.getCookies())) {
      return null;
    }
    return Stream.of(request.getCookies()).filter(c -> c.getName().equals(GAME_TOKEN)).findFirst().orElse(null);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);

    chain.doFilter(request, response);

    // gameService.logToken();
  }
}
