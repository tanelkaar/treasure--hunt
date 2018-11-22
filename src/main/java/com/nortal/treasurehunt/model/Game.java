package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.enums.GameState;
import com.nortal.treasurehunt.enums.TeamState;
import com.nortal.treasurehunt.util.CoordinatesUtil;
import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class Game {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Game.class);

  private Long id;
  private final String name;
  private final Coordinates startFinish;
  private final List<Challenge> challenges;
  private final List<Team> teams = new ArrayList<>();
  private GameState state = GameState.RUNNING;

  public Game(String name, Coordinates startFinish, List<Challenge> challenges) {
    this.id = IDUtil.getNext();
    this.name = name;
    this.startFinish = startFinish;
    this.challenges = challenges;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Coordinates getStartFinish() {
    return startFinish;
  }

  public List<Challenge> getChallenges() {
    return challenges;
  }

  public List<Team> getTeams() {
    return teams;
  }

  public TeamDTO addTeam(TeamDTO dto) {
    synchronized (teams) {
      Team team = getTeams().stream()
          .filter(i -> StringUtils.equalsIgnoreCase(i.getName(), dto.getName()))
          .findFirst()
          .orElse(null);

      if (team != null) {
        throw new TreasurehuntException(ErrorCode.TEAM_EXISTS);
      }
      teams.add(team =
          new Team(dto.getName(), CoordinatesUtil.randomize(startFinish), CoordinatesUtil.randomize(startFinish)));
      dto.setId(team.getId());
    }
    return dto;
  }

  public Team getTeam(Long teamId) {
    Team team = teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().orElse(null);
    if (team == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_TEAM);
    }
    return team;
  }

  public void start(Long teamId, Member member) {
    getTeam(teamId).addMember(member);
  }

  public GameMap getMap(Long teamId) {
    Team team = getTeam(teamId);

    TrailLog log = team.getLatestLog();
    Coordinates location = log != null ? log.getCoordinates() : null;
    switch (team.getState()) {
    case STARTING:
      return new GameMap(location, new Waypoint(team.getStart()), null, null, team.getState());
    case IN_PROGRESS:
      if (challenges.stream().allMatch(c -> team.isCompleted(c.getId()))) {
        team.setState(TeamState.COMPLETING);
        return new GameMap(location, null, new Waypoint(team.getFinish()), null, team.getState());
      }
      List<Waypoint> waypoints = new ArrayList<>();
      challenges.forEach(c -> {
        ChallengeResponse response = team.getResponse(c.getId());
        ChallengeWaypoint wp =
            new ChallengeWaypoint(c, response != null ? response.getState() : ChallengeState.UNCOMPLETED);
        waypoints.add(wp);
      });
      return new GameMap(location, null, null, waypoints, TeamState.IN_PROGRESS);
    case COMPLETING:
      return new GameMap(location, null, new Waypoint(team.getFinish()), null, team.getState());
    case COMPLETED:
      return new GameMap(location, null, null, null, team.getState());
    default:
      throw new TreasurehuntException(ErrorCode.UNEXPECTED_ERROR);
    }
  }

  private Challenge getChallenge(Long challengeId) {
    Challenge challenge = challenges.stream().filter(c -> c.getId().equals(challengeId)).findFirst().orElse(null);
    if (challenge == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_CHALLENGE);
    }
    return challenge;
  }

  public Challenge startChallenge(Long teamId, Long challengeId) {
    Challenge challenge = getChallenge(challengeId);
    // if (!CoordinatesUtil
    // .intersects(new Boundaries(challenge.getCoordinates(),
    // Challenge.CHALLENGE_BOUNDARIES_MARGIN_METERS), coords)) {
    // throw new TreasurehuntException(ErrorCode.CHALLENGE_NOT_IN_RANGE);
    // }
    getTeam(teamId).startChallenge(challenge.getId());
    return challenge;
  }

  public void completeChallenge(Long teamId, ChallengeResponse response) {
    Challenge challenge = getChallenge(response.getChallengeId());
    getTeam(teamId).completeChallenge(challenge.getId(), response);
  }

  public GameMap sendLocation(Long teamId, String memberId, Coordinates coords) {
    Team team = getTeam(teamId);
    team.sendLocation(memberId, coords);
    challenges.stream()
        .filter(c -> CoordinatesUtil.intersects(new Boundaries(c.getCoordinates(), Waypoint.WAYPOINT_RANGE), coords)
            && !team.isCompleted(c.getId()))
        .forEach(c -> team.createChallenge(c.getId()));
    return getMap(teamId);
  }

  public boolean isRunning() {
    return GameState.RUNNING.equals(state);
  }

  // public Game validate(GameAuthData authData) {
  // if (authData == null || !Objects.equals(id, authData.getGameId())) {
  // throw new TreasurehuntException(ErrorCode.INVALID_GAME);
  // }
  // if (!isRunning()) {
  // throw new TreasurehuntException(ErrorCode.INVALID_GAME_STATE);
  // }
  // Team team = getTeam(authData.getTeamId());
  // if (team == null) {
  // throw new TreasurehuntException(ErrorCode.INVALID_TEAM);
  // }
  // team.validate(authData);
  // return this;
  // }
}
