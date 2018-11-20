package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.security.GameAuthFilter;
import com.nortal.treasurehunt.service.GameService;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice(assignableTypes = GameController.class)
public class GameAuthDataAdvice implements ResponseBodyAdvice<Object> {
  private static final Logger LOG = LoggerFactory.getLogger(GameAuthDataAdvice.class);

  @Resource
  private GameService gameService;

  @Override
  public Object beforeBodyWrite(@Nullable Object body,
      MethodParameter returnType,
      MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType,
      ServerHttpRequest request,
      ServerHttpResponse response) {
    refreshAuthCookie(response);
    return body;
  }

  @Override
  public boolean supports(MethodParameter params, Class<? extends HttpMessageConverter<?>> arg1) {
    return true;
  }

  private void refreshAuthCookie(ServerHttpResponse response) {
    LOG.info("refresh cookie");
    Cookie cookie = new Cookie(GameAuthFilter.GAME_TOKEN, gameService.getAuthToken());
    cookie.setPath("/");
    ((ServletServerHttpResponse) response).getServletResponse().addCookie(cookie);
  }
}
