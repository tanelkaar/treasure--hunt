package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.service.GameTokenService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
  @Resource
  private GameTokenService tokenService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    tokenService.readToken(request);
    return true;
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
    tokenService.writeToken(((ServletServerHttpResponse) response).getServletResponse());
    return body;
  }
}
