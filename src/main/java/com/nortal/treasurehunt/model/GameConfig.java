package com.nortal.treasurehunt.model;

public class GameConfig {
  private String name;
  private Coordinates start;

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
}
