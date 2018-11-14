package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.Challenge.ChallengeType;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.model.Waypoint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  @Resource
  private MemberService memberService;

  private List<Game> games = new ArrayList<>();
  // private Game game;

  private boolean isValidForGame(MemberDTO member) {
    return member != null && member.getMemberId() != null && member.getGameId() != null && member.getTeamId() != null;
  }

  private void validate(MemberDTO member) {
    System.out
        .println(String.format("validate: %s %d %d", member.getMemberId(), member.getGameId(), member.getTeamId()));
    boolean valid = false;
    try {
      valid = getGame(member.getGameId()).getTeam(member.getTeamId()).getMember(member.getMemberId()) != null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (!valid) {
      throw new RuntimeException("Member info is invalid!");
    }
  }

  public List<GameDTO> getGames() {
    List<GameDTO> result = new ArrayList<>();
    this.games.forEach(g -> {
      result.add(convert(g));
    });
    return result;
  }

  private GameDTO convert(Game game) {
    GameDTO result = new GameDTO();
    BeanUtils.copyProperties(game, result, "teams");

    List<TeamDTO> teams = new ArrayList<>();
    game.getTeams().forEach(t -> {
      teams.add(convert(t));
    });
    result.setTeams(teams);
    return result;
  }

  private TeamDTO convert(Team team) {
    TeamDTO result = new TeamDTO();
    BeanUtils.copyProperties(team, result);
    return result;
  }

  public GameDTO addGame(GameConfig config) {
    if (config == null) {
      return null;
    }
    Game game = null;
    synchronized (games) {
      game = this.games.stream()
          .filter(g -> StringUtils.equalsIgnoreCase(g.getName(), config.getName()))
          .findFirst()
          .orElse(null);
      if (game == null) {
        game = new Game(config.getName(), config.getStart(), this.generateChallenges(config.getStart()));
        this.games.add(game);
      }
    }
    return convert(game);
  }

  public TeamDTO addTeam(Long gameId, TeamDTO team) {
    if ( gameId == null || team == null) {
      return null;
    }
    Game game = this.getGame(gameId);
    if (game == null) {
      return null;
    }
    Team t = game.getTeams()
        .stream()
        .filter(i -> StringUtils.equalsIgnoreCase(i.getName(), team.getName()))
        .findFirst()
        .orElse(null);

    if (t == null) {
      game.getTeams().add(t = new Team(team.getName()));
      team.setId(t.getId());
    }
    return team;
  }

  public void start(MemberDTO member) {
    if (!isValidForGame(member)) {
      return;
    }
    this.getGame(member.getGameId())
        .getTeam(member.getTeamId())
        .addMember(memberService.getMember(member.getMemberId()));
  }

  private Game getGame(Long gameId) {
    return games.stream().filter(g -> g.getId().equals(gameId)).findFirst().orElse(null);
  }

  // public boolean isRunning() {
  // return game != null;
  // }
  //
  // public void startGame(GameConfig config) {
  // if (config == null) {
  // return;
  // }
  // this.game = new Game(config.getName(), config.getStart(),
  // generateChallenges(config.getStart()));
  // }
  //
  // public Long selectTeam() {
  // if (!isRunning()) {
  // return null;
  // }
  // return 1L; // TODO: this should create/select team and member somehow
  // }
  //
  public GameMap getMap(MemberDTO member) {
    validate(member);
    Game game = this.getGame(member.getGameId());
    List<Waypoint> waypoints = new ArrayList<>();
    game.getChallenges().forEach(c -> {
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
