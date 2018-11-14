package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.List;

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

  public void addTeam(Team team) {
    teams.add(team);
  }

  public Team getTeam(Long teamId) {
    return teams.stream().filter(t -> t.getId().equals(teamId)).findFirst().orElse(null);
  }
}
