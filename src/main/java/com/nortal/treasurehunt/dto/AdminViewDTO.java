package com.nortal.treasurehunt.dto;

import com.nortal.treasurehunt.model.Coordinates;
import java.util.List;

public class AdminViewDTO {
  private List<Coordinates> challenges;
  private List<TeamDTO> teams;
  public List<Coordinates> getChallenges() {
    return challenges;
  }
  public void setChallenges(List<Coordinates> challenges) {
    this.challenges = challenges;
  }
  public List<TeamDTO> getTeams() {
    return teams;
  }
  public void setTeams(List<TeamDTO> teams) {
    this.teams = teams;
  }

}
