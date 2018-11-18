package com.nortal.treasurehunt.serialization;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.nortal.treasurehunt.enums.ChallengeAnswerType;
import com.nortal.treasurehunt.model.Challenge;
import com.nortal.treasurehunt.model.ChallengeOption;
import com.nortal.treasurehunt.model.Coordinates;
import com.nortal.treasurehunt.model.Game;
import com.nortal.treasurehunt.model.Member;
import com.nortal.treasurehunt.model.Team;
import com.nortal.treasurehunt.model.TrailLog;
import com.nortal.treasurehunt.util.GameSerializationUtil;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class GameSerializationUtilTest {
  @Test
  public void testGameSerialization() {
    Game game = createTestGame();
    String result = new Gson().toJson(game);
    assert result != null;
  }

  private Game createTestGame() {
    Game game =
        new Game("game1", new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(-77.988496046)), new ArrayList<>());
    Team team1 = new Team("team1");
    Member member1 = createTestMember("id1", "Member Name I");
    team1.addMember(member1);
    team1.setState(Team.TeamState.STARTING);
    team1.logTrail(new TrailLog(new Coordinates(BigDecimal.valueOf(-65.0014), BigDecimal.valueOf(99.4378))));

    Team team2 = new Team("teäm2");
    team2.getMembers().add(createTestMember("id2", "name2"));
    team2.getMembers().add(createTestMember("id3", "awefaw"));
    game.getTeams().add(team1);
    game.getTeams().add(team2);

    Challenge challenge = createTestChallenge();
    game.getChallenges().add(challenge);
    return game;
  }

  private static Member createTestMember(String id, String name) {
    Member member = new Member();
    member.setId(id);
    member.setName(name);
    return member;
  }

  private Challenge createTestChallenge() {
    Challenge challenge = new Challenge();
    ChallengeOption option = new ChallengeOption(1L, "opt1");
    challenge.getOptions().add(option);
    challenge.setAnswerType(ChallengeAnswerType.TEXT);
    challenge.setText("challengeText1");
    challenge.setCoordinates(new Coordinates(BigDecimal.valueOf(80.00068648), BigDecimal.valueOf(70.988496046)));
    return challenge;
  }

  @Test
  public void testGameDeserialization() throws IOException {
    Game game = GameSerializationUtil.deserializeFromJSON(
        Files.lines(Paths.get("src/test/serialized_game_1.json"))
            .collect(Collectors.joining()));
    assert game != null;
    assert game.getTeams().size() == 2;
    assert game.getTeams().get(1).getName().equals("teäm2");
    assert game.getChallenges().get(0).getCoordinates().getLat().equals(BigDecimal.valueOf(80.00068648));
    assert game.getChallenges().get(0).getAnswerType() == ChallengeAnswerType.TEXT;
  }
}
