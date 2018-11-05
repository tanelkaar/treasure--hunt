package com.nortal.treasurehunt.model;

public class TrailLog {

  private final Coordinates coordinates;
  private Team team;
  private final Member member;
  private long timestamp;
  private boolean primary;

  public TrailLog(Member member, Coordinates coordinates) {
    this.member = member;
    this.coordinates = coordinates;
    this.timestamp = System.currentTimeMillis();

    if(member.getTeam() != null) {
      team = member.getTeam();
      if(member.equals(team.getPrimaryMember())) {
        primary = true;
      } else if(team.getLastUpdateTimestamp() < timestamp - 60*1000) {
        team.setPrimaryMember(member);
        primary = true;
      }

      if(primary) {
        team.logTrail(this);
      }
      member.logTrail(this);
    }
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public Team getTeam() {
    return team;
  }

  public Member getMember() {
    return member;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public boolean isPrimary() {
    return primary;
  }
}
