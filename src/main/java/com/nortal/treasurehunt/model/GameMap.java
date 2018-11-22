package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.enums.TeamState;
import java.util.List;

public class GameMap {
  private Coordinates location;
  private Waypoint start;
  private Waypoint finish;
  private List<Waypoint> waypoints;
  private TeamState state;

  public GameMap(Coordinates location, Waypoint start, Waypoint finish, List<Waypoint> waypoints, TeamState state) {
    this.location = location;
    this.start = start;
    this.finish = finish;
    this.waypoints = waypoints;
    this.state = state;
  }

  public Coordinates getLocation() {
    return location;
  }

  public Waypoint getStart() {
    return start;
  }

  public Waypoint getFinish() {
    return finish;
  }

  public List<Waypoint> getWaypoints() {
    return waypoints;
  }

  public TeamState getState() {
    return state;
  }
}
