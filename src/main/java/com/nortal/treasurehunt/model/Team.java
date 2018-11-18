package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.MemberDTO;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.util.IDUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
  private String primaryMemberId;
  private final List<TeamChallenge> completedChallenges = new ArrayList<>();
  private TeamState state = TeamState.STARTING;
  private long lastUpdateTimestamp = 0L;
  private final List<TrailLog> trail = new ArrayList<>();
  private List<ChallengeResponse> responses = new ArrayList<>();

  public enum TeamState {
    STARTING, IN_PROGRESS, COMPLETED;
  }

  public Team(String name) {
    this.id = IDUtil.getNext();
    this.name = name;
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
    return members.stream().filter(

        m -> m.getId().equals(memberId)).findFirst().orElse(null);

  }

  public ChallengeResponse getResponse(Long challengeId) {
    return responses.stream().filter(

        r -> r.getChallengeId().equals(challengeId)).findFirst().orElse(null);

  }

  public void completeChallenge(ChallengeResponse response) {
    synchronized (responses) {
      if (getResponse(response.getChallengeId()) != null) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_COMPLETED);
      }
      responses.add(response);
    }
  }

  public synchronized void sendLocation(MemberDTO authMember, Coordinates coords) {
    validate(authMember);

    if (trail.isEmpty()) {
      this.primaryMemberId = authMember.getMemberId();
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

    if (!StringUtils.equals(primaryMemberId, authMember.getMemberId())) {
      if (duration.getSeconds() <= PRIMARY_MEMBER_TIMEOUT) {
        LOG.info("Skipping team {} trail log update - member {} is not valid to update log",
            name,
            authMember.getMemberId());
        return;
      }
      LOG.info("Replacing team {} primary member {} with {}", name, primaryMemberId, authMember.getMemberId());
      this.primaryMemberId = authMember.getMemberId();
    }
    updateTrailLog(coords);
  }

  private void updateTrailLog(Coordinates coords) {
    LOG.info("Updated team {} trail by member {}", name, primaryMemberId);
    trail.add(new TrailLog(coords));
  }

  public Team validate(MemberDTO authMember) {
    if (authMember == null || !Objects.equals(id, authMember.getTeamId())
        || getMember(authMember.getMemberId()) == null) {
      throw new TreasurehuntException(ErrorCode.UNAUTHORIZED_MEMBER);
    }
    return this;
  }
}