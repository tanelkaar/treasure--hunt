package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.service.GameService;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(assignableTypes = GameController.class)
public class GameTokenAdvice extends HandlerInterceptorAdapter implements ResponseBodyAdvice<Object> {
  private static final Logger LOG = LoggerFactory.getLogger(GameTokenAdvice.class);
  public static final String GAME_TOKEN = "game-token";

  @Resource
  private GameService gameService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    Cookie cookie = getTokenCookie(request);
    gameService.initToken(cookie != null ? cookie.getValue() : null);
    return true;
  }

  private Cookie getTokenCookie(HttpServletRequest request) {
    if (ArrayUtils.isEmpty(request.getCookies())) {
      return null;
    }
    return Stream.of(request.getCookies()).filter(c -> c.getName().equals(GAME_TOKEN)).findFirst().orElse(null);
  }

  @Override
  public boolean supports(MethodParameter params, Class<? extends HttpMessageConverter<?>> arg1) {
    return true;
  }

  @Override
  public Object beforeBodyWrite(@Nullable Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {

    Cookie cookie = new Cookie(GAME_TOKEN, gameService.getTokenJwt());
    cookie.setPath("/");
    ((ServletServerHttpResponse) response).getServletResponse().addCookie(cookie);

    return body;
  }
}
