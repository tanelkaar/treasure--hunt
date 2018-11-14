package com.nortal.treasurehunt.dto;

public class MemberDTO {
  private String memberId;
  private Long gameId;
  private Long teamId;

  public MemberDTO() {
  }

  public MemberDTO(String memberId) {
    this(memberId, null, null);
  }

  public MemberDTO(String memberId, Long gameId, Long teamId) {
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
