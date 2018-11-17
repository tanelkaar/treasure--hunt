package com.nortal.treasurehunt.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.service.MemberService;
import java.io.IOException;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class MemberAuthFilter extends AbstractAuthenticationProcessingFilter {
  private static final Logger LOG = LoggerFactory.getLogger(MemberAuthFilter.class);

  @Resource
  private MemberService memberService;

  private static final String AUHT_TOKEN = "auth-token";
  private static final Algorithm ALGORITHM = Algorithm.HMAC256("treasurehunt");

  public MemberAuthFilter(String path) {
    super(path);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException, ServletException {

    Authentication auth = authenticate(getAuthCookie(request));

    // missing or invalid cookie - create member
    if (auth == null) {
      auth =
          getAuthenticationManager().authenticate(new MemberAuth(new MemberDTO(memberService.createMember().getId())));
    }

    if (!auth.isAuthenticated()) {
      throw new BadCredentialsException("Unable to authenticate!");
    }
    return auth;
  }

  public Authentication authenticate(Cookie authCookie) {
    MemberDTO member = null;
    if (authCookie == null || (member = readAndVerify(authCookie)) == null) {
      return null;
    }
    Authentication auth = getAuthenticationManager().authenticate(new MemberAuth(member));
    return auth.isAuthenticated() ? auth : null;
  }

  public MemberDTO readAndVerify(Cookie authCookie) {
    if (authCookie == null) {
      return null;
    }

    String authToken = authCookie.getValue();
    try {
      DecodedJWT jwt = JWT.require(ALGORITHM).build().verify(authCookie.getValue());
      return new MemberDTO(jwt.getClaim("memberId").asString(),
          jwt.getClaim("gameId").asLong(),
          jwt.getClaim("teamId").asLong());
    } catch (Exception e) {
      LOG.warn(String.format("Unable to verify authToken: %s", authToken), e);
    }
    return null;
  }

  private Cookie getAuthCookie(HttpServletRequest request) {
    if (ArrayUtils.isEmpty(request.getCookies())) {
      return null;
    }
    return Stream.of(request.getCookies()).filter(c -> c.getName().equals(AUHT_TOKEN)).findFirst().orElse(null);
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authResult) throws IOException, ServletException {
    SecurityContextHolder.getContext().setAuthentication(authResult);
    System.out.println("is authenticated: " + authResult.isAuthenticated());
    chain.doFilter(request, response);

    refreshAuthCookie(request, response);
  }

  private void refreshAuthCookie(HttpServletRequest request, HttpServletResponse response) {
    MemberDTO member = ((MemberAuth) SecurityContextHolder.getContext().getAuthentication()).getDetails();

    String authToken = JWT.create()
        .withClaim("memberId", member.getMemberId())
        .withClaim("gameId", member.getGameId())
        .withClaim("teamId", member.getTeamId())
        .sign(ALGORITHM);

    Cookie authCookie = ObjectUtils.defaultIfNull(getAuthCookie(request), new Cookie(AUHT_TOKEN, authToken));
    authCookie.setPath("/");
    authCookie.setValue(authToken);
    response.addCookie(authCookie);
  }
}
