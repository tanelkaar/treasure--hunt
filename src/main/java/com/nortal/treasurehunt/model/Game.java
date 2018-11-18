package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class Game {
  private Long id;
  private final String name;
  private final Coordinates startFinish;
  private final List<Challenge> challenges;
  private final List<Team> teams = new ArrayList<>();

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
      teams.add(team = new Team(dto.getName()));
      dto.setId(team.getId());
    }
    return dto;
  }

  public Team getTeam(Long teamId) {
    return teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().orElse(null);
  }

  public void addMember(Long teamId, Member member) {
    getTeam(teamId).addMember(member);
  }

  public GameMap getMap(Long teamId) {
    Team team = getTeam(teamId);

    List<Waypoint> waypoints = new ArrayList<>();
    challenges.forEach(c -> {
      waypoints.add(new Waypoint(c, team.getResponse(c.getId()) != null));
    });
    return new GameMap(getStartFinish(), waypoints);
  }

  public Challenge getChallenge(Long challengeId) {
    return challenges.stream().filter(c -> c.getId().equals(challengeId)).findFirst().orElse(null);
  }

  public Challenge startChallenge(Long teamId, Long challengeId, Coordinates coords) {
    Challenge challenge = getChallenge(challengeId);
    // if (!CoordinatesUtil.intersects(challenge.getBoundaries(), coords)) {
    // return;
    // }
    return challenge;
  }

  public void completeChallenge(Long teamId, ChallengeResponse response) {
    getTeam(teamId).completeChallenge(response);
  }

  public void sendLocation(MemberDTO authMember, Coordinates coords) {
    validate(authMember).getTeam(authMember.getTeamId()).sendLocation(authMember, coords);
  }

  public Game validate(MemberDTO authMember) {
    if (authMember == null || !Objects.equals(id, authMember.getGameId()) || getTeam(authMember.getTeamId()) == null) {
      throw new TreasurehuntException(ErrorCode.UNAUTHORIZED_MEMBER);
    }
    return this;
  }
}
