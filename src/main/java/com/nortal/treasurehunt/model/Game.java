package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.enums.GameState;
import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class Game {

  private Long id;
  private final String name;
  private final Coordinates startFinish;
  private final List<Challenge> challenges;
  private List<Team> teams = new ArrayList<>();
  private GameState state = GameState.RUNNING;
  private List<Coordinates> startingPoints = new ArrayList<>();

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
    if (teams == null) {
      teams = new ArrayList<>();
    }
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
      Coordinates start = CollectionUtils.isNotEmpty(getStartingPoints()) ? getStartingPoints().remove(0) : startFinish;
      getTeams().add(team = new Team(dto.getName(), start, startFinish, getChallenges()));
      dto.setId(team.getId());
    }
    return dto;
  }

  public Team getTeam(Long teamId) {
    Team team = getTeams().stream().filter(t -> t.getId().equals(teamId)).findFirst().orElse(null);
    if (team == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_TEAM);
    }
    return team;
  }

  public void start(Long teamId, Member member) {
    getTeam(teamId).addMember(member);
  }

  public boolean isRunning() {
    return GameState.RUNNING.equals(state);
  }

  public List<Coordinates> getStartingPoints() {
    if (startingPoints == null) {
      this.startingPoints = new ArrayList<>();
    }
    return startingPoints;
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
