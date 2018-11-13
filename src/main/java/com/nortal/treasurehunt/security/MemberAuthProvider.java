package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.service.MemeberService;
import javax.annotation.Resource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MemberAuthProvider implements AuthenticationProvider {
  @Resource
  private MemeberService memeberService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    if (memeberService.hasMember((String) authentication.getPrincipal())) {
      return authentication;
    }
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return MemberAuth.class.isAssignableFrom(authentication);
  }
}
