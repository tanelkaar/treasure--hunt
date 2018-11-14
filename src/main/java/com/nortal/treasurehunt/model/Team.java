package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.model.TeamChallenge.ChallengeState;
import com.nortal.treasurehunt.util.CoordinatesUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team {

  private final String name;
  private final String id;
  private final Map<String,Member> members = new HashMap<>();
  private Member primaryMember;
  private final Map<String, Challenge> uncompletedChallenges;
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
    this.id = UUID.randomUUID().toString();
    game.addTeam(this);
  }

  public enum TeamState {
    STARTING,
    IN_PROGRESS,
    COMPLETED;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public List<Member> getMembersOrderedByName() {
    return members.values().stream()
        .sorted(Comparator.comparing(Member::getName))
        .collect(Collectors.toList());
  }

  public void addMember(Member member) {
    this.members.put(member.getId(), member);
    if (primaryMember == null) {
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
    if (currentChallenge == null) {
      currentChallenge = new TeamChallenge(this, challenge);
    }
    currentChallenge.setStartTimestamp(System.currentTimeMillis());
    currentChallenge.setState(ChallengeState.IN_PROGRESS);
  }

  public void completeCurrentChallenge(String result) {
    if (currentChallenge == null) {
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
    if (currentChallenge == null) {
      for (Challenge challenge : uncompletedChallenges.values()) {
        if (CoordinatesUtil.intersects(challenge.getBoundaries(), trailLog.getCoordinates())) {
          startChallenge(challenge);
          break;
        }
      }
    }
  }

  public List<Challenge> getUncompletedChallenges() {
    return new ArrayList<>(uncompletedChallenges.values());
  }

  public Game getGame() {
    return game;
  }

  public boolean hasMember(String memberId) {
    return members.containsKey(memberId);
  }

}