package com.nortal.treasurehunt.model;

public class Waypoint {
  private Long challengeId;
  private Coordinates coords;
  private boolean visited;

  public Waypoint(Challenge challenge) {
    this.challengeId = challenge.getId();
    this.coords = challenge.getCoordinates();
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }

  public Coordinates getCoords() {
    return coords;
  }

  public void setCoords(Coordinates coords) {
    this.coords = coords;
  }

  public boolean isVisited() {
    return visited;
  }

  public void setVisited(boolean visited) {
    this.visited = visited;
  }
}