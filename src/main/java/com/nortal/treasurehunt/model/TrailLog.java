package com.nortal.treasurehunt.model;

import java.util.Date;

public class TrailLog {
  private final Coordinates coordinates;
  private Date date;

  public TrailLog(Coordinates coordinates) {
    this.coordinates = coordinates;
    this.date = new Date();
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public Date getDate() {
    return date;
  }
}
