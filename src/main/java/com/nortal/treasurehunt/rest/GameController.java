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

  @PostMapping("/register")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<Void> register() {
    GameToken token = gameService.getToken();
    // reset auth member game for now - later we should init game info according
    // to server state
    // token.setGameId(null);
    // token.setTeamId(null);
    LOG.info("REGISTER");
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/list")
  public ResponseEntity<List<GameDTO>> getGames() {
    return ResponseEntity.ok(gameService.getGames());
  }

  @PostMapping("/add")
  public ResponseEntity<GameDTO> addGame(@RequestBody GameConfig config) {
    return ResponseEntity.ok(gameService.addGame(config));
  }

  @PostMapping("/{gameId}/team/add")
  public ResponseEntity<TeamDTO> addTeam(@PathVariable("gameId") Long gameId, @RequestBody TeamDTO team) {
    return ResponseEntity.ok(gameService.addTeam(gameId, team));
  }

  @PostMapping("/start")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<Void> start(@RequestBody GameToken token) {
    gameService.start(token.getGameId(), token.getTeamId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/map")
  public ResponseEntity<GameMap> getMap() {
    return ResponseEntity.ok(gameService.getMap());
  }

  @PostMapping("/challenge/{challengeId}/start")
  public ResponseEntity<Challenge> startChallenge(@PathVariable("challengeId") Long challengeId) {
    return ResponseEntity.ok(gameService.getCurrentChallenge());
  }

  @PostMapping("/challenge/{challengeId}/complete")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<Void> completeChallenge(@PathVariable("challengeId") Long challengeId,
      @RequestBody ChallengeResponse response) {
    gameService.completeChallenge(response);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/location")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<GameMap> sendLocation(@RequestBody Coordinates coords) {
    GameToken token = gameService.getToken();
    if (token.getGameId() == null) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(gameService.sendLocation(coords));
  }

  @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json")
  public String exportGame() {
    return GameSerializationUtil.serializeToJSON(gameService.getGame());
  }

  @PostMapping("/game/import")
  @ResponseStatus(code = HttpStatus.OK)
  public void importGame(@RequestBody String jsonGameData) {
    Game game = GameSerializationUtil.deserializeFromJSON(jsonGameData);
    gameService.addGame(game);
  }
}
