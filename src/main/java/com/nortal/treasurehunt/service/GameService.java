package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.Challenge.ChallengeType;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Member;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.model.TeamChallenge;
import com.nortal.treasurehunt.model.TrailLog;
import com.nortal.treasurehunt.model.Waypoint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GameService {

  @Resource
  MemberService memberService;

  private Game game;

  public boolean isRunning() {
    return game != null;
  }

  public void startGame(GameConfig config) {
    if (config == null) {
      return;
    }
    this.game = new Game(config.getName(),
                         config.getStart(),
                         generateChallenges(config.getStart()));
  }

  public List<Team> getTeams() {
    return game.getTeamsOrderedByName();
  }

  public String selectTeam(String teamName, String memberId) {
    if (!isRunning()) {
      return null;
    }
    assert teamName != null;
    Member member = memberService.getMember(memberId);
    Team team = addMemberToTeam(teamName, member);
    return team.getId();
  }

  private Team addMemberToTeam(String teamName, Member member) {
    Team team = game.getTeamByName(teamName);
    if (team == null) {
      team = createTeam(teamName, member);
    }
    team.addMember(member);
    member.setTeam(team);
    return team;
  }

  private Team createTeam(String name, Member primaryMember) {
    return new Team(name, primaryMember, game);
  }

  public GameMap getMap(Long memberId) {
    if (!isRunning()) {
      return null;
    }
    List<Waypoint> waypoints = new ArrayList<>();
    this.game.getChallenges().values().forEach(c -> {
      // TODO: figure out visited by member
      waypoints.add(new Waypoint(c));
    });
    return new GameMap(game.getStartFinish(), waypoints);
  }

  private Map<String, Challenge> generateChallenges(Coordinates coords) {
    Map<String, Challenge> challenges = new HashMap();
    for (int i = 0; i <= 5; i++) {
      String id = UUID.randomUUID().toString();
      challenges.put(id, new Challenge(id,
                                       "challenge" + i,
                                       new Coordinates(getRandomPos(coords.getLat()),
                                                       getRandomPos(coords.getLng())),
                                       ChallengeType.values()[(int) Math.random() * 3],
                                       "text"));
    }
    return challenges;
  }

  private BigDecimal getRandomPos(BigDecimal pos) {
    long multip = ((int) Math.random() * 2) % 2 == 0 ? 1 : -1;
    return pos.add(BigDecimal.valueOf(Math.random() / 500).multiply(BigDecimal.valueOf(multip)));
  }

  public Challenge startChallenge(String memberId, String challengeId) {
    Team team = getTeamByMemberId(memberId);
    Challenge challenge = game.getChallenges().get(challengeId);
    if (team == null || challenge == null) {
      return null;
    }
    TeamChallenge teamChallenge = new TeamChallenge(team, challenge);
    team.setCurrentChallenge(teamChallenge);
    return challenge;
  }

  public Challenge getCurrentChallenge(String memberId) {
    Team team = getTeamByMemberId(memberId);
    if (team == null || team.getCurrentChallenge() == null) {
      return null;
    }
    return team.getCurrentChallenge().getChallenge();
  }

  private Team getTeamByMemberId(String memberId) {
    return game.getTeamsOrderedByName().stream()
        .filter(t -> t.hasMember(memberId))
        .findFirst()
        .orElse(null);
  }

  public List<Coordinates> getMarkers(String memberId) {
    Team team = getTeamByMemberId(memberId);
    if (team == null) {
      return null;
    }
    List<Coordinates> coords = new ArrayList<>();
    if (team.getCurrentChallenge() != null) {
      coords.add(team.getCurrentChallenge().getChallenge().getCoordinates());
    } else if (team.getUncompletedChallenges().size() > 0) {
      coords.addAll(
          team.getUncompletedChallenges().stream()
              .map(Challenge::getCoordinates)
              .collect(Collectors.toList()));
    } else {
      coords.add(game.getStartFinish());
    }
    return coords;
  }

  public void logTrail(String memberId, Coordinates coords) {
    TrailLog trailLog = new TrailLog(memberService.getMember(memberId),
                                     coords);
    // TODO
    // If the entry is primary and is less that 10m away from a challenge
    // without a team_challenge entry,
    // create a team_challenge entry and mark that challenge for that team
  }
}
