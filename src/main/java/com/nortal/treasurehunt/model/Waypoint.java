package com.nortal.treasurehunt.model;

public class Waypoint {
  public static final int WAYPOINT_RANGE = 20;

  private Coordinates coords;
  private int range = WAYPOINT_RANGE;

  public Waypoint(Coordinates coords) {
    this.coords = coords;
  }

  public Coordinates getCoords() {
    return coords;
  }

  public int getRange() {
    return range;
  }
}
