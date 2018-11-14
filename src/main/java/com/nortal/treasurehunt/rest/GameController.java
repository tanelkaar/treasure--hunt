package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {
  @Resource
  private GameService gameService;

  @GetMapping("/register-member")
  public ResponseEntity<String> registerMember() {
    String memberId = UUID.randomUUID().toString();
    System.out.println(String.format("memberId: %s", memberId));
    return ResponseEntity.ok("1");
  }

  @GetMapping("/is-running")
  public ResponseEntity<Boolean> isRunning() {
    return ResponseEntity.ok(gameService.isRunning());
  }

  @PostMapping("/start")
  public void start(@RequestBody GameConfig config) {
    gameService.startGame(config);
  }

  @GetMapping("/teams")
  public ResponseEntity<List<Team>> getTeams() {
    return ResponseEntity.ok(gameService.getTeams());
  }

  @PostMapping("/member/{memberId}/select-team")
  public ResponseEntity<String> selectTeam(@PathVariable("memberId") String memberId,
                                           @RequestParam("teamName") String teamName) {
    return ResponseEntity.ok(gameService.selectTeam(teamName, memberId));
  }

  @GetMapping("/member/{memberId}/map")
  public ResponseEntity<GameMap> getMap(@PathVariable("memberId") Long memberId) {
    return ResponseEntity.ok(gameService.getMap(memberId));
  }

  @PostMapping("/member/{memberId}/challenge/{challengeId}")
  public ResponseEntity<Challenge> startChallenge(@PathVariable("memberId") String memberId,
                                                  @PathVariable("challengeId") String challengeId) {
    Challenge challenge = gameService.startChallenge(memberId, challengeId);
    return challenge != null ? ResponseEntity.ok(challenge) : ResponseEntity.notFound().build();
  }

  @GetMapping("/current-challenge/{memberId}")
  public ResponseEntity<Challenge> getCurrentChallenge(@PathVariable("memberId") String memberId) {
    return ResponseEntity.ok(gameService.getCurrentChallenge(memberId));
  }

  @GetMapping("/member/{memberId}/get-markers")
  public ResponseEntity<List<Coordinates>> getMarkers(@PathVariable String memberId) {
    List<Coordinates> coords = gameService.getMarkers(memberId);
    return coords == null || coords.isEmpty() ?  ResponseEntity.notFound().build()
                                              : ResponseEntity.ok(coords);
  }

  @PostMapping("/member/{memberId}/log-trail")
  public ResponseEntity<Boolean> logTrail(@PathVariable String memberId,
                                 @RequestBody Coordinates coords) {
    gameService.logTrail(memberId, coords);
    return ResponseEntity.ok(true);
  }
}
