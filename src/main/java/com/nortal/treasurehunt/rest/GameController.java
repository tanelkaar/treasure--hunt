package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.ChallengeResponse;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.security.MemberAuth;
import com.nortal.treasurehunt.service.GameService;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {
  private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

  @Resource
  private GameService gameService;

  @PostMapping("/register")
  @ResponseStatus(code = HttpStatus.OK)
  public void register() {
    LOG.info(String.format("memberId: %s",
        ((MemberAuth) SecurityContextHolder.getContext().getAuthentication()).getDetails().getMemberId()));
  }

  @GetMapping("/games")
  public ResponseEntity<List<GameDTO>> getGames() {
    return ResponseEntity.ok(gameService.getGames());
  }

  @PostMapping("/add-game")
  public ResponseEntity<GameDTO> addGame(@RequestBody GameConfig config) {
    return ResponseEntity.ok(gameService.addGame(config));
  }

  @PostMapping("/game/{gameId}/add-team")
  public ResponseEntity<TeamDTO> addTeam(@PathVariable("gameId") Long gameId, @RequestBody TeamDTO team) {
    return ResponseEntity.ok(gameService.addTeam(gameId, team));
  }

  @PostMapping("/game/start")
  @ResponseStatus(code = HttpStatus.OK)
  public void start(@RequestBody MemberDTO member) {
    MemberDTO authMember = getAuthMember();
    authMember.setGameId(member.getGameId());
    authMember.setTeamId(member.getTeamId());
    gameService.start(authMember);
  }

  @GetMapping("/game/map")
  public ResponseEntity<GameMap> getMap() {
    return ResponseEntity.ok(gameService.getMap(getAuthMember()));
  }

  @PostMapping("/game/challenge/{challengeId}/start")
  public ResponseEntity<Challenge> startChallenge(@PathVariable("challengeId") Long challengeId,
      @RequestBody Coordinates coords) {
    return ResponseEntity.ok(gameService.startChallenge(getAuthMember(), challengeId, coords));
  }

  @PostMapping("/game/challenge/{challengeId}/complete")
  @ResponseStatus(code = HttpStatus.OK)
  public void completeChallenge(@PathVariable("challengeId") Long challengeId,
      @RequestBody ChallengeResponse response) {
    response.setChallengeId(challengeId);
    gameService.completeChallenge(getAuthMember(), response);
  }

  @PostMapping("/game/send-location")
  @ResponseStatus(code = HttpStatus.OK)
  public void completeChallenge(@RequestBody Coordinates coords) {
    gameService.sendLocation(getAuthMember(), coords);
  }

  private MemberDTO getAuthMember() {
    return ((MemberAuth) SecurityContextHolder.getContext().getAuthentication()).getDetails();
  }
}
