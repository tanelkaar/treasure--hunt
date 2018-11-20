package com.nortal.treasurehunt.config;

import com.nortal.treasurehunt.security.GameAuthFilter;
import com.nortal.treasurehunt.security.GameAuthProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .disable()
        .csrf()
        .disable()
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .requestCache()
        .disable()
        .addFilterBefore(memberAuthFilter(), UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests()
        .antMatchers("/api/**")
        .authenticated();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
    authManagerBuilder.authenticationProvider(authProvider());
  }

  @Bean
  public FilterRegistrationBean<GameAuthFilter> filterRegistrationBean() throws Exception {
    FilterRegistrationBean<GameAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setEnabled(false);
    filterRegistrationBean.setFilter(memberAuthFilter());
    return filterRegistrationBean;
  }

  @Bean
  public GameAuthFilter memberAuthFilter() throws Exception {
    GameAuthFilter maf = new GameAuthFilter("/api/game/**");
    maf.setAuthenticationManager(authenticationManager());
    return maf;
  }

  @Bean
  public GameAuthProvider authProvider() {
    return new GameAuthProvider();
  }
}
