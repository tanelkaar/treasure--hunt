package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.service.MemberService;
import javax.annotation.Resource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MemberAuthProvider implements AuthenticationProvider {
  @Resource
  private MemberService memberService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (memberService.hasMember((String) authentication.getPrincipal())) {
      return authentication;
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return MemberAuth.class.isAssignableFrom(authentication);
  }
}
