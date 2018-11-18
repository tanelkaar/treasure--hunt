package com.nortal.treasurehunt.model;

import java.util.ArrayList;
import java.util.List;

public class Member {

  private String id;
  private String name;
  private final List<TrailLog> trail = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void logTrail(TrailLog trailLog) {
    trail.add(trailLog);
  }

  public List<TrailLog> getTrail() {
    return trail;
  }
}
