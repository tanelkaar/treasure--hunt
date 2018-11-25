package com.nortal.treasurehunt.dto;

import com.nortal.treasurehunt.model.Coordinates;
import java.util.List;

public class TeamDTO {
  private Long id;
  private String name;
  private List<Coordinates> trail;

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

  public List<Coordinates> getTrail() {
    return trail;
  }

  public void setTrail(List<Coordinates> trail) {
    this.trail = trail;
  }

}
