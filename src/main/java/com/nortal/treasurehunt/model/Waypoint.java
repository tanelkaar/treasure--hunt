package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ChallengeType;

public class Waypoint {
  private Long challengeId;
  private Coordinates coords;
  private int range;
  private ChallengeType type;
  private ChallengeState state;

  public Waypoint(Challenge challenge, ChallengeState state) {
    this.challengeId = challenge.getId();
    this.coords = challenge.getCoordinates();
    this.range = Challenge.CHALLENGE_BOUNDARIES_MARGIN_METERS;
    this.type = challenge.getType();
    this.state = state;
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public Coordinates getCoords() {
    return coords;
  }

  public int getRange() {
    return range;
  }

  public ChallengeType getType() {
    return type;
  }

  public ChallengeState getState() {
    return state;
  }
}
