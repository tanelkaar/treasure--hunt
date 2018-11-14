package com.nortal.treasurehunt.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Member {

  public Member() {
  }

  private String id;
  private String name;
  private Team team;
  private final List<TrailLog> trail = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Team getTeam() {
    return team;
  }

  public void setTeam(Team team) {
    this.team = team;
  }

  public void logTrail(TrailLog trailLog) {
    trail.add(trailLog);
  }

  public List<TrailLog> getTrail() {
    return trail;
  }
}
