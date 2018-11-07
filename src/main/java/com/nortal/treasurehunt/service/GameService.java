package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.Challenge.ChallengeType;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Waypoint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  private Game game;

  public boolean isRunning() {
    return game != null;
  }

  public void startGame(GameConfig config) {
    if (config == null) {
      return;
    }
    this.game = new Game(config.getName(), config.getStart(), generateChallenges(config.getStart()));
  }

  public Long selectTeam() {
    if (!isRunning()) {
      return null;
    }
    return 1L; // TODO: this should create/select team and member somehow
  }

  public GameMap getMap(Long memberId) {
    if (!isRunning()) {
      return null;
    }
    List<Waypoint> waypoints = new ArrayList<>();
    this.game.getChallenges().forEach(c -> {
      // TODO: figure out visited by member
      waypoints.add(new Waypoint(c));
    });
    return new GameMap(game.getStartFinish(), waypoints);
  }

  private List<Challenge> generateChallenges(Coordinates coords) {
    List<Challenge> challenges = new ArrayList<>();
    for (int i = 0; i <= 5; i++) {
      challenges.add(new Challenge(Long.valueOf(i),
                                   "challenge" + i,
                                   new Coordinates(getRandomPos(coords.getLat()), getRandomPos(coords.getLng())),
                                   ChallengeType.values()[(int) Math.random() * 3],
                                   "text"));
    }
    return challenges;
  }

  private BigDecimal getRandomPos(BigDecimal pos) {
    long multip = ((int) Math.random() * 2) % 2 == 0 ? 1 : -1;
    return pos.add(BigDecimal.valueOf(Math.random() / 500).multiply(BigDecimal.valueOf(multip)));
  }
}
