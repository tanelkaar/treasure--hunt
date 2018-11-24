package com.nortal.treasurehunt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.rest.GameTokenContext;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameTokenService {
  private static final Logger LOG = LoggerFactory.getLogger(GameService.class);
  private static final Algorithm ALGORITHM = Algorithm.HMAC256("treasurehunt");
  public static final String GAME_TOKEN = "game-token";

  @Resource
  private GameService gameService;

  public void readToken(HttpServletRequest request) {
    GameToken token = getToken(request);
    if (isValid(token)) {
      GameTokenContext.set(token);
      return;
    }
    LOG.warn("invalid token was provided");
    logToken(token);

    GameTokenContext.set(getValid(token));
    throw new TreasurehuntException(ErrorCode.INVALID_TOKEN);
  }

  public void writeToken(HttpServletResponse response) {
    String jwt = getTokenJwt();
    Cookie cookie = new Cookie(GAME_TOKEN, jwt);
    cookie.setPath("/");
    cookie.setMaxAge(StringUtils.isBlank(jwt) ? 0 : 3600);
    response.addCookie(cookie);
  }

  public GameToken getToken(HttpServletRequest request) {
    return readToken(getTokenCookie(request));
  }

  private Cookie getTokenCookie(HttpServletRequest request) {
    if (ArrayUtils.isEmpty(request.getCookies())) {
      return null;
    }
    return Stream.of(request.getCookies()).filter(c -> c.getName().equals(GAME_TOKEN)).findFirst().orElse(null);

  }

  private GameToken readToken(Cookie cookie) {
    if (cookie == null) {
      return null;
    }

    String tokenJwt = cookie.getValue();
    try {
      DecodedJWT jwt = JWT.require(ALGORITHM).build().verify(tokenJwt);
      return new GameToken(jwt.getClaim("memberId").asString(),
          jwt.getClaim("gameId").asLong(),
          jwt.getClaim("teamId").asLong());
    } catch (Exception e) {
      LOG.error(String.format("invalid jwt=%s", tokenJwt), e);
    }
    return null;
  }

  public boolean isValid(GameToken token) {
    if (!isValidMember(token)) {
      return false;
    }

    if (token.getGameId() != null) {
      try {
        gameService.getGame(token.getGameId()).getTeam(token.getTeamId()).getMember(token.getMemberId());
      } catch (Exception e) {
        return false;
      }
    }
    return true;
  }

  public boolean isValidMember(GameToken token) {
    return token != null && gameService.getMember(token.getMemberId()) != null;
  }

  private GameToken getValid(GameToken token) {
    if (isValid(token)) {
      return token;
    }
    if (isValidMember(token)) {
      token.setGameId(null);
      token.setTeamId(null);
      return token;
    }
    return null;
  }

  public GameToken getToken() {
    return GameTokenContext.get();
  }

  private String getTokenJwt() {
    GameToken token = getValid(getToken());
    if (token == null) {
      return null;
    }
    return JWT.create()
        .withClaim("memberId", token.getMemberId())
        .withClaim("gameId", token.getGameId())
        .withClaim("teamId", token.getTeamId())
        .sign(ALGORITHM);
  }

  public void logToken() {
    logToken(getToken());
  }

  public void logToken(GameToken token) {
    if (token == null) {
      LOG.info("no token info");
      return;
    }
    LOG.info("token memberId={}, gameId={}, teamId={}", token.getMemberId(), token.getGameId(), token.getTeamId());
  }
}
