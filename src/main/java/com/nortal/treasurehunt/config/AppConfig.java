package com.nortal.treasurehunt.config;

import com.nortal.treasurehunt.rest.GameTokenAdvice;
import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
  @Resource
  private GameTokenAdvice tokenAdvice;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(tokenAdvice).addPathPatterns("/api/game/**");
  }
}
