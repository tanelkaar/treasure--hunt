package com.nortal.treasurehunt.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class MemberAuth extends AbstractAuthenticationToken {
  private String memberId;
  private String jwt;

  public MemberAuth(String memberId, String jwt) {
    super(null);
    this.memberId = memberId;
    this.jwt = jwt;
  }

  @Override
  public Object getCredentials() {
    return jwt;
  }

  @Override
  public Object getPrincipal() {
    return memberId;
  }

}
