package com.nortal.treasurehunt.service;

import com.nortal.treasurehunt.TreasurehuntException;
import com.nortal.treasurehunt.dto.GameDTO;
import com.nortal.treasurehunt.dto.MemberDTO;
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
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.util.IDUtil;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class GameService {
  @Resource
  private MemberService memberService;

  private List<Game> games = new ArrayList<>();
  // private Game game;

  private void validate(MemberDTO member) {
    if (member == null) {
      throw new TreasurehuntException(ErrorCode.INVALID_MEMBER);
    }

    boolean valid = false;
    try {
      valid = getGame(member.getGameId()).getTeam(member.getTeamId()).getMember(member.getMemberId()) != null;
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (!valid) {
      throw new TreasurehuntException(ErrorCode.INVALID_MEMBER,
          String.format("invalid member: gameId=%d, teamId=%d, memberId=%s",
              member.getGameId(),
              member.getTeamId(),
              member.getMemberId()));
    }
  }

  public List<GameDTO> getGames() {
    List<GameDTO> result = new ArrayList<>();
    this.games.forEach(g -> {
      result.add(convert(g));
    });
    return result;

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

  public TeamDTO addTeam(Long gameId, TeamDTO team) {
    return this. getGame(gameId).addTeam(team);
  }

  public void start(MemberDTO member) {
    getGame(member.getGameId()).addMember(member.getTeamId(), memberService.getMember(member.getMemberId()));
  }

  public GameMap getMap(MemberDTO member) {
    validate(member);
    return getGame(member.getGameId()).getMap(member.getTeamId());
  }

  public Challenge startChallenge(MemberDTO member, Long challengeId, Coordinates coords) {
    validate(member);
    return getGame(member.getGameId()).startChallenge(member.getTeamId(), challengeId, coords);
  }

  public void completeChallenge(MemberDTO member, ChallengeResponse response) {
    validate(member);
    getGame(member.getGameId()).completeChallenge(member.getTeamId(), response);
  }

  public void sendLocation(MemberDTO authMember, Coordinates coords) {
    getGame(authMember.getGameId()).sendLocation(authMember, coords);
  }

  private Game getGame(Long gameId) {
    return games.stream().filter(g -> g.getId().equals(gameId)).findFirst().orElse(null);
  }

  private List<Challenge> generateChallenges(Coordinates coords) {
    List<Challenge> challenges = new ArrayList<>();
    Challenge c = new Challenge();
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
