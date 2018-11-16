package com.nortal.treasurehunt.model;

import java.util.List;

public class GameMap {
  private Coordinates start;
  private Coordinates finish;
  private List<Waypoint> waypoints;

  public GameMap(Coordinates start, List<Waypoint> waypoints) {
    this(start, start, waypoints);
  }

  public GameMap(Coordinates start, Coordinates finish, List<Waypoint> waypoints) {
    this.start = start;
    this.finish = finish;
    this.waypoints = waypoints;
  }

  public Coordinates getStart() {
    return start;
  }

  public Coordinates getFinish() {
    return finish;
  }

  public List<Waypoint> getWaypoints() {
    return waypoints;
  }
}
