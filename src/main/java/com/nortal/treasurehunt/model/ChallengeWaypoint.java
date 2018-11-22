package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ChallengeType;

public class ChallengeWaypoint extends Waypoint {
  private Long challengeId;
  private ChallengeType type;
  private ChallengeState state;

  public ChallengeWaypoint(Challenge challenge, ChallengeState state) {
    super(challenge.getCoordinates());
    this.challengeId = challenge.getId();
    this.type = challenge.getType();
    this.state = state;
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public ChallengeType getType() {
    return type;
  }

  public ChallengeState getState() {
    return state;
  }
}
