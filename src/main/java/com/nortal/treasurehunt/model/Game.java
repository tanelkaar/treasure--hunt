package com.nortal.treasurehunt.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Game {

  private final String name;
  private final Coordinates startFinish;
  private final Map<String, Challenge> challenges;
  private final Map<String,Team> teamsByName = new HashMap<>();
  private final Map<String,Team> teamsById = new HashMap<>();

  public Game(String name, Coordinates startFinish,
              Map<String, Challenge> challenges) {
    this.name = name;
    this.startFinish = startFinish;
    this.challenges = challenges;
  }

  public String getName() {
    return name;
  }

  public Coordinates getStartFinish() {
    return startFinish;
  }

  public Map<String, Challenge> getChallenges() {
    return challenges;
  }

  public Team getTeamByName(String name) {
    return teamsByName.get(name);
  }

  public List<Team> getTeamsOrderedByName() {
    return teamsByName.values().stream()
        .sorted(Comparator.comparing(Team::getName))
        .collect(Collectors.toList());
  }

  public Map<String,Team> getTeamsById() { return teamsById; }

  void addTeam(Team team) {
    teamsByName.put(team.getName(), team);
    teamsById.put(team.getId(), team);
  }
}
