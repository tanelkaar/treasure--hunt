package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.enums.TeamState;
import com.nortal.treasurehunt.util.CoordinatesUtil;
import com.nortal.treasurehunt.util.IDUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Team {
  private static final Logger LOG = LoggerFactory.getLogger(Team.class);
  private static final long LOG_UPDATE_INTERVAL = 15;
  private static final long PRIMARY_MEMBER_TIMEOUT = 60;

  private Long id;
  private final String name;
  private final List<Member> members = new ArrayList<>();
  private final Coordinates start;
  private final Coordinates finish;
  private final Boundaries startBoundaries;
  private final Boundaries finishBoundaries;
  private String primaryMemberId;
  private TeamState state = TeamState.STARTING;
  private final List<TrailLog> trail = new ArrayList<>();
  private TeamChallenge currentChallenge;
  private final List<Challenge> uncompletedChallenges;
  private final List<TeamChallenge> completedChallenges = new ArrayList<>();

  public Team(String name, Coordinates start, Coordinates finish, List<Challenge> uncompletedChallenges) {
    this.id = IDUtil.getNext();
    this.name = name;
    this.start = start;
    this.finish = finish;
    this.startBoundaries = new Boundaries(start, Waypoint.WAYPOINT_RANGE);
    this.finishBoundaries = new Boundaries(finish, Waypoint.WAYPOINT_RANGE);
    this.uncompletedChallenges = uncompletedChallenges;
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
      if (members.stream().anyMatch(

          m -> m.getId().equals(member.getId())))

      {
        return;
      }
      this.members.add(member);
    }
  }

  public Coordinates getStart() {
    return start;
  }

  public Coordinates getFinish() {
    return finish;
  }

  public TeamState getState() {
    return state;
  }

  public void setState(TeamState state) {
    this.state = state;
  }

  public List<TrailLog> getTrail() {
    return trail;
  }

  public Member getMember(String memberId) {
    Member member = members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);
    if (member == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_MEMBER);
    }
    return member;
  }

  public void startChallenge(Challenge challenge) {
    synchronized (this) {
      if (!uncompletedChallenges.contains(challenge)
          || currentChallenge != null && currentChallenge.getChallenge().equals(challenge)) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_COMPLETED);
      }
      uncompletedChallenges.remove(challenge);
      // if current challenge wasn't chosen yet
      if (currentChallenge == null) {
        this.currentChallenge = new TeamChallenge(challenge);
      } else {
        if (!currentChallenge.getChallenge().equals(challenge)) {
          throw new TreasurehuntException(ErrorCode.WRONG_CHALLENGE);
        }
      }
      currentChallenge.setState(com.nortal.treasurehunt.model.TeamChallenge.ChallengeState.IN_PROGRESS);
      currentChallenge.setStartTimestamp(System.currentTimeMillis());
      // should be done once, but doesn't matter
      state = TeamState.IN_PROGRESS;
    }
  }

  public void completeChallenge(ChallengeResponse response) {
    synchronized (this) {
      if (currentChallenge == null) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_NOT_STARTED);
      }
    }
    currentChallenge.setEndTimestamp(System.currentTimeMillis());
    currentChallenge.setState(com.nortal.treasurehunt.model.TeamChallenge.ChallengeState.COMPLETED);
    currentChallenge.setChallengeResponse(response);
    completedChallenges.add(currentChallenge);
    currentChallenge = null;
    if (uncompletedChallenges.isEmpty()) {
      this.state = TeamState.COMPLETING;
    }
  }

  public synchronized GameMap sendLocation(String memberId, Coordinates coords) {
    switch (state) {
    case STARTING:
      if (CoordinatesUtil.intersects(startBoundaries, coords)) {
        this.state = TeamState.IN_PROGRESS;
      }
      break;
    case IN_PROGRESS:
      Challenge challenge = uncompletedChallenges.stream()
      .filter(c -> CoordinatesUtil.intersects(c.getBoundaries(), coords))
      .findFirst().orElse(null);
      if(challenge != null) {
        startChallenge(challenge);
      }
      break;
    case COMPLETING:
      if (CoordinatesUtil.intersects(finishBoundaries, coords)) {
        this.state = TeamState.COMPLETED;
      }
      break;
    case COMPLETED:
    }
    addTrail(memberId, coords);
    return getMap();
  }

  private void addTrail(String memberId, Coordinates coords) {
    Member member = getMember(memberId);

    if (trail.isEmpty()) {
      this.primaryMemberId = member.getId();
      updateTrailLog(coords);
      return;
    }

    TrailLog lastLog = trail.get(trail.size() - 1);
    Duration duration = Duration.between(LocalDateTime.ofInstant(lastLog.getDate().toInstant(), ZoneId.systemDefault()),
        LocalDateTime.now());
    if (CollectionUtils.isNotEmpty(trail)) {
      if (duration.getSeconds() < LOG_UPDATE_INTERVAL) {
        LOG.info("Skipping team {} trail log update - update interval is not passed", name);
        return;
      }
    }

    if (!StringUtils.equals(primaryMemberId, member.getId())) {
      if (duration.getSeconds() <= PRIMARY_MEMBER_TIMEOUT) {
        LOG.info("Skipping team {} trail log update - member {} is not valid to update log", name, member.getId());
        return;
      }
      LOG.info("Replacing team {} primary member {} with {}", name, primaryMemberId, member.getId());
      this.primaryMemberId = member.getId();
    }
    updateTrailLog(coords);
  }

  private void updateTrailLog(Coordinates coords) {
    LOG.info("Updated team {} trail by member {}", name, primaryMemberId);
    trail.add(new TrailLog(coords));
  }

  public TrailLog getLatestLog() {
    synchronized (trail) {
      if (CollectionUtils.isNotEmpty(trail)) {
        return trail.get(trail.size() - 1);
      }
    }
    return null;
  }

  // public Team validate(GameAuthData authData) {
  // if (authData == null || !Objects.equals(id, authData.getTeamId())) {
  // throw new TreasurehuntException(ErrorCode.INVALID_TEAM);
  // }
  // if (getMember(authData.getMemberId()) == null) {
  // throw new TreasurehuntException(ErrorCode.INVALID_MEMBER);
  // }
  // return this;
  // }
  public List<Waypoint> getWaypointsOnMap() {
    List<Waypoint> waypoints = new ArrayList<>();
    // a challenge has been selected (game start) or is in progress. Return
    // that single challenge.
    if (currentChallenge != null) {
      waypoints.add(new ChallengeWaypoint(currentChallenge.getChallenge(),
          state == TeamState.STARTING ? ChallengeState.UNCOMPLETED : ChallengeState.IN_PROGRESS));
    } else {
      // return uncompleted and completed challenges
      uncompletedChallenges.forEach(c -> waypoints.add(new ChallengeWaypoint(c, ChallengeState.UNCOMPLETED)));
      completedChallenges.forEach(c -> waypoints.add(new ChallengeWaypoint(c.getChallenge(), ChallengeState.COMPLETED)));
    }
    return waypoints;
  }

  public GameMap getMap() {
    return new GameMap(this);
  }

  public Challenge getCurrentChallenge() {
    return currentChallenge == null ? null : currentChallenge.getChallenge();
  }

  public Coordinates getCurrentLocation() {
    TrailLog log = getLatestLog();
    return log != null ? log.getCoordinates() : null;
  }
}