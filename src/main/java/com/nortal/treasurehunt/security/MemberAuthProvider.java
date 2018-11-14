package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.service.MemberService;
import javax.annotation.Resource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MemberAuthProvider implements AuthenticationProvider {
  @Resource
  private MemberService memeberService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    MemberDTO memberInfo = ((MemberAuth) authentication).getDetails();
    authentication.setAuthenticated(memeberService.isValid(memberInfo));
    return authentication;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return MemberAuth.class.isAssignableFrom(authentication);
  }
}
