package com.nortal.treasurehunt.model;

import java.util.ArrayList;
import java.util.List;

public class GameConfig {
  private String name;
  private Coordinates start;
  private Coordinates finish;
  private List<Challenge> challenges = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Coordinates getStart() {
    return start;
  }

  public void setStart(Coordinates start) {
    this.start = start;
  }

  public Coordinates getFinish() {
    return finish;
  }

  public void setFinish(Coordinates finish) {
    this.finish = finish;
  }

  public List<Challenge> getChallenges() {
    return challenges;
  }

  public void setChallenges(List<Challenge> challenges) {
    this.challenges = challenges;
  }
}
