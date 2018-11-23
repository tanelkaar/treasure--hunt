package com.nortal.treasurehunt.model;

public class GameToken {
  private String memberId;
  private Long gameId;
  private Long teamId;

  public GameToken() {
  }

  public GameToken(String memberId) {
    this(memberId, null, null);
  }

  public GameToken(String memberId, Long gameId, Long teamId) {
    this.memberId = memberId;
    this.gameId = gameId;
    this.teamId = teamId;
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
}
