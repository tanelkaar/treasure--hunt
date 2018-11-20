package com.nortal.treasurehunt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.enums.ChallengeAnswerType;
import com.nortal.treasurehunt.enums.ChallengeState;
import com.nortal.treasurehunt.enums.ChallengeType;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.ChallengeOption;
import com.nortal.treasurehunt.model.ChallengeResponse;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.Member;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.security.GameAuth;
import com.nortal.treasurehunt.security.GameAuthData;
import com.nortal.treasurehunt.util.IDUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

  private static final Algorithm ALGORITHM = Algorithm.HMAC256("treasurehunt");

  private List<Game> games = new ArrayList<>();
  private List<Member> members = new ArrayList<>();

  public boolean isValid(GameAuthData member) {
    if (member == null || member.getMemberId() == null) {
      return false;
    }
    return getMember(member.getMemberId()) != null;
  }

  public Member createMember() {
    LOG.info("Creating new member - having {} members so far", members.size());
    Member member = new Member();
    member.setId(UUID.randomUUID().toString());
    synchronized (members) {
      members.add(member);
    }
    return member;
  }

  public Member getMember(String memberId) {
    return members.stream().filter(m -> m.getId().equals(memberId)).findFirst().orElse(null);
  }

  public GameAuthData getAuthData(String authToken) {
    DecodedJWT jwt = JWT.require(ALGORITHM).build().verify(authToken);
    return new GameAuthData(jwt.getClaim("memberId").asString(),
        jwt.getClaim("gameId").asLong(),
        jwt.getClaim("teamId").asLong(),
        jwt.getClaim("challengeId").asLong());
  }

  public GameAuthData getAuthData() {
    return ((GameAuth) SecurityContextHolder.getContext().getAuthentication()).getDetails();
  }

  public String getAuthToken() {
    GameAuthData authData = prepareAuthData();
    return JWT.create()
        .withClaim("memberId", authData.getMemberId())
        .withClaim("gameId", authData.getGameId())
        .withClaim("teamId", authData.getTeamId())
        .withClaim("challengeId", authData.getChallengeId())
        .sign(ALGORITHM);
  }

  public void logAuthData() {
    GameAuthData authData = getAuthData();
    LOG.info("Auth data: memberId={}, gameId={}, teamId={}, challengeId={}",
        authData.getMemberId(),
        authData.getGameId(),
        authData.getTeamId(),
        authData.getChallengeId());
  }

  public List<GameDTO> getGames() {
    List<GameDTO> result = new ArrayList<>();
    this.games.forEach(g -> {
      result.add(convert(g));
    });
    return result;
  }

  public Game getGame(Long gameId) {
    Game game = games.stream().filter(g -> g.getId().equals(gameId)).findFirst().orElse(null);
    if (game == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_GAME);
    }
    return game;
  }

  private Game getGame() {
    GameAuthData authData = getAuthData();
    Game game = getGame(authData.getGameId());
    game.getTeam(authData.getTeamId()).getMember(authData.getMemberId());
    return game;
  }

  private GameAuthData prepareAuthData() {
    GameAuthData authData = getAuthData();
    if (authData.getGameId() == null) {
      return authData;
    }

    Game game = getGame();
    // authData.setGameId(game.getId()); -- avoid for now to access main
    Team team = game.getTeam(authData.getTeamId());
    // authData.setTeamId(team.getId()); -- avoid for now to access main
    ChallengeResponse response = team.getResponse(ChallengeState.IN_PROGRESS);
    authData.setChallengeId(response != null ? response.getChallengeId() : null);
    return authData;
  }

  private GameDTO convert(Game game) {
    GameDTO result = new GameDTO();
    BeanUtils.copyProperties(game, result, "teams");

    List<TeamDTO> teams = new ArrayList<>();
    game.getTeams().forEach(t -> {
      teams.add(convert(t));
    });
    result.setTeams(teams);
    return result;

  }

  private TeamDTO convert(Team team) {
    TeamDTO result = new TeamDTO();
    BeanUtils.copyProperties(team, result);
    return result;
  }

  public GameDTO addGame(GameConfig config) {
    if (config == null) {
      return null;
    }
    Game game = null;
    synchronized (games) {
      game = this.games.stream()
          .filter(g -> StringUtils.equalsIgnoreCase(g.getName(), config.getName()))
          .findFirst()
          .orElse(null);
      if (game != null) {
        throw new TreasurehuntException(ErrorCode.GAME_EXISTS);
      }
      List<Challenge> challenges = CollectionUtils.isNotEmpty(config.getChallenges()) ? config.getChallenges()
          : generateChallenges(config.getStart());
      game = new Game(config.getName(), config.getStart(), challenges);
      this.games.add(game);
    }
    return convert(game);
  }

  public void addGame(Game game) {
    synchronized (games) {
      games.add(game);
    }
  }

  public TeamDTO addTeam(Long gameId, TeamDTO team) {
    return getGame(gameId).addTeam(team);
  }

  public void start(Long gameId, Long teamId) {
    GameAuthData authData = getAuthData();
    authData.setGameId(gameId);
    authData.setTeamId(teamId);
    getGame(gameId).getTeam(teamId).addMember(getMember(authData.getMemberId()));
  }

  public GameMap getMap() {
    return getGame().getMap(getAuthData().getTeamId());
  }

  public Challenge startChallenge(Long challengeId, Coordinates coords) {
    return getGame().startChallenge(getAuthData().getTeamId(), challengeId, coords);
  }

  public void completeChallenge(ChallengeResponse response) {
    getGame().completeChallenge(getAuthData().getTeamId(), response);
  }

  public GameMap sendLocation(Coordinates coords) {
    return getGame().sendLocation(getAuthData().getTeamId(), getAuthData().getMemberId(), coords);
  }

  private List<Challenge> generateChallenges(Coordinates coords) {
    List<Challenge> challenges = new ArrayList<>();
    Challenge c = new Challenge();
    BeanUtils.copyProperties(tempChallenges.get((int) (Math.random() * 5)), c);
    c.setId(IDUtil.getNext());
    c.setCoordinates(coords);
    challenges.add(c);
    c = new Challenge();
    BeanUtils.copyProperties(tempChallenges.get((int) (Math.random() * 5)), c);
    c.setId(IDUtil.getNext());
    c.setCoordinates(coords);
    challenges.add(c);
    for (int i = 0; i <= 5; i++) {
      c = new Challenge();
      BeanUtils.copyProperties(tempChallenges.get((int) (Math.random() * 5)), c);
      c.setId(IDUtil.getNext());
      c.setCoordinates(new Coordinates(getPos(coords.getLat()), getPos(coords.getLng())));
      challenges.add(c);
    }
    return challenges;
  }

  private BigDecimal getPos(BigDecimal pos) {
    long multip = ((int) (Math.random() * 2)) % 2 == 0 ? 1 : -1;
    return pos.add(BigDecimal.valueOf(Math.random() / 500).multiply(BigDecimal.valueOf(multip)));
  }

  List<Challenge> tempChallenges = new ArrayList<>();
  {
    Challenge c = new Challenge();
    c.setType(ChallengeType.QUESTION);
    c.setText("Kus on pilt tehtud?");
    c.setImage("https://drive.google.com/uc?id=1z8i7Hurg0oq3deTziNbjoGg1iAKkkmr0");
    c.setAnswerType(ChallengeAnswerType.TEXT);
    tempChallenges.add(c);

    c = new Challenge();
    c.setType(ChallengeType.QUESTION);
    c.setText("Kumb oli enne, kas muna või kana?");
    c.setOptions(
        Arrays.asList(new ChallengeOption(IDUtil.getNext(), "kana"), new ChallengeOption(IDUtil.getNext(), "muna")));
    c.setAnswerType(ChallengeAnswerType.SINGLE_CHOICE);
    tempChallenges.add(c);

    c = new Challenge();
    c.setType(ChallengeType.QUESTION);
    c.setText("Kessee laulab köögis?");
    c.setVideo("https://www.youtube.com/embed/FIH5gF1z4SY?rel=0");
    c.setOptions(Arrays.asList(new ChallengeOption(IDUtil.getNext(), "kukk"),
        new ChallengeOption(IDUtil.getNext(), "kana"),
        new ChallengeOption(IDUtil.getNext(), "justament")));
    c.setAnswerType(ChallengeAnswerType.MULTI_CHOICE);
    tempChallenges.add(c);

    c = new Challenge();
    c.setType(ChallengeType.TASK);
    c.setText("Mõtle välja mingi äge luuletus!");
    c.setAnswerType(ChallengeAnswerType.TEXT);
    tempChallenges.add(c);

    c = new Challenge();
    c.setType(ChallengeType.TASK);
    c.setText("Tee meeskonnast pilt!");
    c.setAnswerType(ChallengeAnswerType.IMAGE);
    tempChallenges.add(c);
  }
}
