package com.nortal.treasurehunt.model;

import java.util.ArrayList;
import java.util.List;

public class ChallengeResponse {
  private Long challengeId;
  private Coordinates coords;
  private String value;
  private List<Long> options = new ArrayList<>();

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

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<Long> getOptions() {
    return options;
  }

  public void setOptions(List<Long> options) {
    this.options = options;
  }
}
