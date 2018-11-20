package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ErrorCode;
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
  private String primaryMemberId;
  private TeamState state = TeamState.STARTING;
  private final List<TrailLog> trail = new ArrayList<>();
  private List<ChallengeResponse> responses = new ArrayList<>();

  public enum TeamState {
    STARTING,
    IN_PROGRESS,
    COMPLETED;
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
      if (members.stream().anyMatch(m -> m.getId().equals(member.getId()))) {
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

  public ChallengeResponse getResponse(Long challengeId) {
    return responses.stream().filter(r -> r.getChallengeId().equals(challengeId)).findFirst().orElse(null);
  }

  public ChallengeResponse getResponse(ChallengeState state) {
    return responses.stream().filter(r -> state.equals(r.getState())).findFirst().orElse(null);
  }

  public void startChallenge(Long challengeId) {
    synchronized (responses) {
      ChallengeResponse response = getResponse(challengeId);
      if (response != null) {
        if (response.isCompleted()) {
          throw new TreasurehuntException(ErrorCode.CHALLENGE_COMPLETED);
        }
        LOG.info("Team {} challenge {} already started - not creating new response", name, challengeId);
        return;
      }
      LOG.info("Creating team {} challenge {}", name, challengeId);
      responses.add(response = new ChallengeResponse());
      response.setChallengeId(challengeId);
      response.setState(ChallengeState.IN_PROGRESS);
    }
  }

  public void completeChallenge(Long challengeId, ChallengeResponse rsp) {
    synchronized (responses) {
      ChallengeResponse response = getResponse(rsp.getChallengeId());
      if (response == null) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_NOT_STARTED);
      }
      if (response.isCompleted()) {
        throw new TreasurehuntException(ErrorCode.CHALLENGE_COMPLETED);
      }
      response.setOptions(rsp.getOptions());
      response.setValue(rsp.getValue());
      response.setCoords(rsp.getCoords());
      response.setState(ChallengeState.COMPLETED);
    }
  }

  public boolean isCompleted(Long challengeId) {
    ChallengeResponse response = getResponse(challengeId);
    return response != null && response.isCompleted();
  }

  public synchronized void sendLocation(String memberId, Coordinates coords) {
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
}