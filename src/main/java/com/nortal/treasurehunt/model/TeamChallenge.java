package com.nortal.treasurehunt.model;

public class TeamChallenge {

  private final Challenge challenge;
  private long startTimestamp;
  private long endTimestamp;
  private ChallengeState state;
  private String result;
  private ChallengeResponse challengeResponse;

  public TeamChallenge(Challenge challenge) {
    this.challenge = challenge;
  }

  public enum ChallengeState {
    /**
     * First challenge is preselected, without start timestamp
     */
    SELECTED,
    IN_PROGRESS,
    COMPLETED;
  }

  public Challenge getChallenge() {
    return challenge;
  }

  public long getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(long startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public long getEndTimestamp() {
    return endTimestamp;
  }

  public void setEndTimestamp(long endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public ChallengeState getState() {
    return state;
  }

  public void setState(ChallengeState state) {
    this.state = state;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public ChallengeResponse getChallengeResponse() {
    return challengeResponse;
  }

  public void setChallengeResponse(ChallengeResponse challengeResponse) {
    this.challengeResponse = challengeResponse;
  }
}
