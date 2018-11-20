package com.nortal.treasurehunt.security;

public class GameAuthData {
  private String memberId;
  private Long gameId;
  private Long teamId;
  private Long challengeId;

  public GameAuthData() {
  }

  public GameAuthData(String memberId) {
    this(memberId, null, null, null);
  }

  public GameAuthData(String memberId, Long gameId, Long teamId, Long challengeId) {
    this.memberId = memberId;
    this.gameId = gameId;
    this.teamId = teamId;
    this.challengeId = challengeId;
  }

  public String getMemberId() {
    return memberId;
  }

  public Long getGameId() {
    return gameId;
  }

  public void setGameId(Long gameId) {
    this.gameId = gameId;
  }

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  public Long getChallengeId() {
    return challengeId;
  }

  public void setChallengeId(Long challengeId) {
    this.challengeId = challengeId;
  }
}
