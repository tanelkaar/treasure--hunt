package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.service.GameService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    return ResponseEntity.ok(new ArrayList<>()); // TODO: implement meh
  }

  @PostMapping("/select-team")
  public ResponseEntity<Long> selectTeam(@RequestBody String rawTeam) {
    return ResponseEntity.ok(gameService.selectTeam());
  }

  @GetMapping("/member/{memberId}/map")
  public ResponseEntity<GameMap> getMap(@PathVariable("memberId") Long memberId) {
    return ResponseEntity.ok(gameService.getMap(memberId));
  }

  @PostMapping("/member/{memberId}/challenge/{challengeId}")
  public ResponseEntity<Challenge> startChallenge(@PathVariable("memberId") Long memberId,
                                                  @PathVariable("challengeId") Long challengeId,
                                                  @RequestBody Coordinates coords) {
    return ResponseEntity.ok(null); // TODO: implement meh
  }
}
