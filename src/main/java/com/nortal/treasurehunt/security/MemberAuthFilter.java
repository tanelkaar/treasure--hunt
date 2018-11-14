package com.nortal.treasurehunt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nortal.treasurehunt.model.Member;
import com.nortal.treasurehunt.service.MemberService;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class MemberAuthFilter extends AbstractAuthenticationProcessingFilter {
  @Resource
  private MemberService memberService;

  public MemberAuthFilter(String pattern) {
    super(pattern);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {
    if (!ArrayUtils.isEmpty(request.getCookies())) {
      Optional<Cookie> cookie = Stream.of(request.getCookies()).filter(c -> c.getName().equals("token")).findFirst();
      if (cookie.isPresent()) {
        String jwtToken = cookie.get().getValue();
        DecodedJWT jwt = JWT.decode(jwtToken);
        String memberId = jwt.getClaim("memberId").asString();
        try {
          return super.getAuthenticationManager().authenticate(new MemberAuth(memberId, jwtToken));
        } catch (AuthenticationException e) {
          e.printStackTrace();
        }
      }
    }

    Member member = memberService.createMember();
    String jwtToken = JWT.create().withClaim("memberId", member.getId()).sign(Algorithm.HMAC256("treasurehunt"));
    Cookie cookie = new Cookie("token", jwtToken);
    cookie.setPath("/");
    response.addCookie(new Cookie("token", jwtToken));
    return super.getAuthenticationManager().authenticate(new MemberAuth(member.getId(), jwtToken));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    chain.doFilter(request, response);
  }
}
