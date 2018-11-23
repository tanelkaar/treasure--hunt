package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.ChallengeResponse;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.service.GameService;
import com.nortal.treasurehunt.service.GameTokenService;
import com.nortal.treasurehunt.util.GameSerializationUtil;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game")
public class GameController {
  private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

  @Resource
  private GameService gameService;
  @Resource
  private GameTokenService gameTokenService;

  @GetMapping("/list")
  public ResponseEntity<List<GameDTO>> getGames() {
    LOG.info("get games");
    return ResponseEntity.ok(gameService.getGames());
  }

  @PostMapping("/add")
  public ResponseEntity<GameDTO> addGame(@RequestBody GameConfig config) {
    LOG.info("add game");
    return ResponseEntity.ok(gameService.addGame(config));
  }

  @PostMapping("/{gameId}/team/add")
  public ResponseEntity<TeamDTO> addTeam(@PathVariable("gameId") Long gameId, @RequestBody TeamDTO team) {
    LOG.info("add team");
    return ResponseEntity.ok(gameService.addTeam(gameId, team));
  }

  @PostMapping("/start")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<Void> start(@RequestBody GameToken token) {
    LOG.info("start");
    gameService.start(token.getGameId(), token.getTeamId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/map")
  public ResponseEntity<GameMap> getMap() {
    LOG.info("get map");
    return ResponseEntity.ok(gameService.getMap());
  }

  @PostMapping("/challenge/start")
  public ResponseEntity<Challenge> startChallenge() {
    LOG.info("start challenge");
    return ResponseEntity.ok(gameService.getCurrentChallenge());
  }

  @PostMapping("/challenge/complete")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<GameMap> completeChallenge(@RequestBody ChallengeResponse response) {
    LOG.info("complete challenge");
    gameService.completeChallenge(response);
    return ResponseEntity.ok(gameService.getMap());
  }

  @PostMapping("/location")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<GameMap> sendLocation(@RequestBody Coordinates coords) {
    LOG.info("send location");
    return ResponseEntity.ok(gameService.sendLocation(coords));
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json")
  public String exportGame() {
    LOG.info("export game");
    return GameSerializationUtil.serializeToJSON(gameService.getGame());
  }

  @PostMapping("/game/import")
  @ResponseStatus(code = HttpStatus.OK)
  public void importGame(@RequestBody String jsonGameData) {
    LOG.info("import game");
    Game game = GameSerializationUtil.deserializeFromJSON(jsonGameData);
    gameService.addGame(game);
  }
}
