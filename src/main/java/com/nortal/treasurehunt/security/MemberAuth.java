package com.nortal.treasurehunt.security;

import com.nortal.treasurehunt.dto.MemberDTO;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class MemberAuth extends AbstractAuthenticationToken {
  public MemberAuth(MemberDTO member) {
    super(null);
    super.setDetails(member);
    super.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return null;
  }

  @Override
  public MemberDTO getDetails() {
    return (MemberDTO) super.getDetails();
  }
}
