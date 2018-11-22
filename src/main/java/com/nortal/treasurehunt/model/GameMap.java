package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.enums.TeamState;
import java.util.List;

public class GameMap {
  private Coordinates location;
  private Waypoint start;
  private Waypoint finish;
  private List<Waypoint> waypoints;
  private TeamState state;

  public GameMap(Team team) {
    this.location = team.getCurrentLocation();
    this.start = TeamState.STARTING.equals(team.getState()) ? new Waypoint(team.getStart()) : null;
    this.finish = TeamState.COMPLETING.equals(team.getState()) ? new Waypoint(team.getFinish()) : null;
    this.waypoints = TeamState.IN_PROGRESS.equals(team.getState()) ? team.getWaypointsOnMap() : null;
    this.state = team.getState();
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
