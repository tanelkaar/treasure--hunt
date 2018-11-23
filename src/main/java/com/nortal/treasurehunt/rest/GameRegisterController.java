package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.service.GameService;
import com.nortal.treasurehunt.service.GameTokenService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
public class GameRegisterController {
  private static final Logger LOG = LoggerFactory.getLogger(GameRegisterController.class);

  @Resource
  private GameService gameService;
  @Resource
  private GameTokenService gameTokenService;

  @PostMapping
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<Void> register(HttpServletRequest request, HttpServletResponse response) {
    LOG.info("register");
    prepareToken(request);
    gameTokenService.writeToken(response);
    return ResponseEntity.noContent().build();
  }

  public void prepareToken(HttpServletRequest request) {
    try {
      gameTokenService.readToken(request);
    } catch (Exception e) {
    }
    if (GameTokenContext.get() == null) {
      GameTokenContext.set(new GameToken(gameService.createMember().getId()));
    }
  }
}
