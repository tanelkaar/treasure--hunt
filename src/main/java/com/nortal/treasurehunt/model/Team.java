package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.model.TeamChallenge.ChallengeState;
import com.nortal.treasurehunt.util.CoordinatesUtil;
import java.util.ArrayList;
import java.util.List;

public class Team {

  private final String name;
  private final List<Member> members = new ArrayList<>();
  private Member primaryMember;
  private final List<Challenge> uncompletedChallenges;
  private final List<TeamChallenge> completedChallenges = new ArrayList<>();
  private TeamChallenge currentChallenge;
  private TeamState state = TeamState.STARTING;
  private long lastUpdateTimestamp = 0L;
  private final List<TrailLog> trail = new ArrayList<>();
  private final Game game;

  public Team(String name, Member primaryMember, Game game) {
    this.name = name;
    this.primaryMember = primaryMember;
    this.game = game;
    this.uncompletedChallenges = game.getChallenges();
  }

  public enum TeamState {
    STARTING,
    IN_PROGRESS,
    COMPLETED;
  }

  public String getName() {
    return name;
  }

  public List<Member> getMembers() {
    return members;
  }

  public void addMember(Member member) {
    this.members.add(member);
    if(primaryMember == null) {
      primaryMember = member;
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

  public void startChallenge(Challenge challenge) {
    uncompletedChallenges.remove(challenge);
    if(currentChallenge == null) {
      currentChallenge = new TeamChallenge(this, challenge);
    }
    currentChallenge.setStartTimestamp(System.currentTimeMillis());
    currentChallenge.setState(ChallengeState.IN_PROGRESS);
  }

  public void completeCurrentChallenge(String result) {
    if(currentChallenge == null) {
      throw new IllegalStateException("Unable to complete current challenge - no challenge started!");
    }
    currentChallenge.setResult(result);
    currentChallenge.setState(ChallengeState.COMPLETED);
    this.completedChallenges.add(currentChallenge);
    this.currentChallenge = null;
  }

  public long getLastUpdateTimestamp() {
    return lastUpdateTimestamp;
  }

  public void setLastUpdateTimestamp(long lastUpdateTimestamp) {
    this.lastUpdateTimestamp = lastUpdateTimestamp;
  }

  public void setCurrentChallenge(TeamChallenge currentChallenge) {
    this.currentChallenge = currentChallenge;
  }

  public TeamChallenge getCurrentChallenge() {
    return currentChallenge;
  }

  public List<TrailLog> getTrail() {
    return trail;
  }

  public void logTrail(TrailLog trailLog) {
    trail.add(trailLog);
    lastUpdateTimestamp = System.currentTimeMillis();
    // TODO - check if new coordinates intersect with uncompleted challenges
    if(currentChallenge == null) {
      for(Challenge challenge: uncompletedChallenges) {
        if(CoordinatesUtil.intersects(challenge.getBoundaries(), trailLog.getCoordinates())) {
          startChallenge(challenge);
          break;
        }
      }
    }
  }

  public List<Challenge> getUncompletedChallenges() {
    return uncompletedChallenges;
  }

  public Game getGame() {
    return game;
  }

}
>>>>>>> Stashed changes
