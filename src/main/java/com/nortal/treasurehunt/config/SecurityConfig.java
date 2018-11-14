package com.nortal.treasurehunt.config;

import com.nortal.treasurehunt.security.MemberAuthFilter;
import com.nortal.treasurehunt.security.MemberAuthProvider;
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
  public FilterRegistrationBean<MemberAuthFilter> filterRegistrationBean() throws Exception {
    FilterRegistrationBean<MemberAuthFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setEnabled(false);
    filterRegistrationBean.setFilter(memberAuthFilter());
    return filterRegistrationBean;
  }

  @Bean
  public MemberAuthFilter memberAuthFilter() throws Exception {
    MemberAuthFilter maf = new MemberAuthFilter("/api/**");
    maf.setAuthenticationManager(authenticationManager());
    return maf;
  }

  @Bean
  public MemberAuthProvider authProvider() {
    return new MemberAuthProvider();
  }
}
