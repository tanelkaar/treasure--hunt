package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.TeamDTO;
import com.nortal.treasurehunt.enums.ChallengeAnswerType;
import com.nortal.treasurehunt.enums.ChallengeType;
import com.nortal.treasurehunt.enums.ErrorCode;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.ChallengeOption;
import com.nortal.treasurehunt.model.ChallengeResponse;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.GameConfig;
import com.nortal.treasurehunt.model.GameMap;
import com.nortal.treasurehunt.model.GameToken;
import com.nortal.treasurehunt.model.Member;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.rest.GameTokenContext;
import com.nortal.treasurehunt.util.CoordinatesUtil;
import com.nortal.treasurehunt.util.IDUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

  private List<Game> games = new ArrayList<>();
  private List<Member> members = new ArrayList<>();

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

  private GameToken getToken() {
    return GameTokenContext.get();
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

  public Game getGame() {
    GameToken token = getToken();
    Game game = getGame(token.getGameId());
    game.getTeam(token.getTeamId()).getMember(token.getMemberId());
    return game;
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
    GameToken token = getToken();
    token.setGameId(gameId);
    token.setTeamId(teamId);
    getGame(gameId).getTeam(teamId).addMember(getMember(token.getMemberId()));
  }

  public GameMap getMap() {
    return getGame().getTeam(getToken().getTeamId()).getMap();
  }

  public Challenge getCurrentChallenge() {
    Challenge challenge = getGame().getTeam(getToken().getTeamId()).getCurrentChallenge();
    if (challenge == null) {
      throw new TreasurehuntException(ErrorCode.CHALLENGE_NOT_STARTED);
    }
    return challenge;
  }

  public void completeChallenge(ChallengeResponse response) {
    getGame().getTeam(getToken().getTeamId()).completeChallenge(response);
  }

  public GameMap sendLocation(Coordinates coords) {
    return getGame().getTeam(getToken().getTeamId()).sendLocation(getToken().getMemberId(), coords);
  }

  private List<Challenge> generateChallenges(Coordinates coords) {
    List<Challenge> challenges = new ArrayList<>();

    for (int i = 0; i <= 5; i++) {
      Challenge c = new Challenge();
      BeanUtils.copyProperties(tempChallenges.get((int) (Math.random() * 6)), c);
      c.setId(IDUtil.getNext());
      c.setCoordinates(CoordinatesUtil.randomize(coords));
      challenges.add(c);
    }
    return challenges;
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
    c.setType(ChallengeType.QUESTION);
    c.setText("Mis uudist?");
    c.setUrl("http://www.delfi.ee");
    c.setAnswerType(ChallengeAnswerType.TEXT);
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
