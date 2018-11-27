package com.nortal.treasurehunt.rest;

import com.nortal.treasurehunt.dto.AdminViewDTO;
import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.model.Game;
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
@RequestMapping("/api/admin")
public class AdminController {
  private static final Logger LOG = LoggerFactory.getLogger(AdminController.class);

  @Resource
  private GameService gameService;

  @RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/json")
  public String exportGame() {
    LOG.info("export game");
    return GameSerializationUtil.serializeToJSON(gameService.getGame());
  }

  @GetMapping("/game/list")
  public ResponseEntity<List<GameDTO>> getGames() {
    LOG.info("get games");
    return ResponseEntity.ok(gameService.getGames());
  }

  @GetMapping("/game/{gameId}/export")
  public String exportGame(@PathVariable("gameId") Long gameId) {
    LOG.info("export game");
    return GameSerializationUtil.serializeToJSON(gameService.getGame(gameId));
  }

  @PostMapping("/import")
  @ResponseStatus(code = HttpStatus.OK)
  public void importGame(@RequestBody String jsonGameData) {
    LOG.info("import game");
    Game game = GameSerializationUtil.deserializeFromJSON(jsonGameData);
    gameService.addGame(game);
  }

  @RequestMapping("/all")
  @ResponseStatus(code = HttpStatus.OK)
  public ResponseEntity<AdminViewDTO> getAdminView() {
    return ResponseEntity.ok(gameService.getAdminView());
  }

  @PostMapping("/clear")
  @ResponseStatus(code = HttpStatus.OK)
  public void clear() {
    LOG.info("clear game");
    gameService.clear();
  }
}
