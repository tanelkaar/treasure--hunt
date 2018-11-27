package com.nortal.treasurehunt.dto;

import com.nortal.treasurehunt.enums.GameState;
import java.util.ArrayList;
import java.util.List;

public class GameDTO {
  private Long id;
  private String name;
  private List<TeamDTO> teams = new ArrayList<>();
  private GameState state;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TeamDTO> getTeams() {
    return teams;
  }

  public void setTeams(List<TeamDTO> teams) {
    this.teams = teams;
  }

  public GameState getState() {
    return state;
  }

  public void setState(GameState state) {
    this.state = state;
  }
}
