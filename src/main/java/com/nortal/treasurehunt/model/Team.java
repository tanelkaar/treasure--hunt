package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.List;

public class Team {
  private Long id;
  private final String name;
  private final List<Member> members = new ArrayList<>();
  private Member primaryMember;
  private final List<TeamChallenge> completedChallenges = new ArrayList<>();
  private TeamState state = TeamState.STARTING;
  private long lastUpdateTimestamp = 0L;
  private final List<TrailLog> trail = new ArrayList<>();
  private List<ChallengeResponse> responses = new ArrayList<>();

  public Team(String name) {
    this(name, null);
  }

  public Team(String name, Member primaryMember) {
    this.id = IDUtil.getNext();
    this.name = name;
    this.primaryMember = primaryMember;
  }

  public enum TeamState {
    STARTING, IN_PROGRESS, COMPLETED;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Member> getMembers() {
    return members;
  }

  public void addMember(Member member) {
    synchronized (members) {
      if (members.stream().anyMatch(m -> m.getId().equals(member.getId()))) {
        return;
      }
      this.members.add(member);
      if (primaryMember == null) {
        primaryMember = member;
      }
    }
  }

  public Member getPrimaryMember() {
    return primaryMember;
  }

  public void setPrimaryMember(Member primaryMember) {
    this.primaryMember = primaryMember;
  }

  public TeamState getState() {
    return state;
  }

  public void setState(TeamState state) {
    this.state = state;
  }

  public List<TeamChallenge> getCompletedChallenges() {
    return completedChallenges;
  }

  public long getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  public List<TrailLog> getTrail() {
    return trail;
  }

  public void logTrail(TrailLog trailLog) {
    trail.add(trailLog);
    lastUpdateTimestamp = System.currentTimeMillis();
    // // TODO - check if new coordinates intersect with uncompleted challenges
    // if (currentChallenge == null) {
    // for (Challenge challenge : uncompletedChallenges) {
    // if (CoordinatesUtil.intersects(challenge.getBoundaries(),
    // trailLog.getCoordinates())) {
    // startChallenge(challenge);
    // break;
    // }
    // }
    // }
  }

  public Member getMember(String memberId) {
    return members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);
  }

  public ChallengeResponse getResponse(Long challengeId) {
    return responses.stream().filter(r -> r.getChallengeId().equals(challengeId)).findFirst().orElse(null);
  }

  public void completeChallenge(ChallengeResponse response) {
    synchronized (responses) {
      if (getResponse(response.getChallengeId()) != null) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_COMPLETED);
      }
      responses.add(response);
    }
  }
}