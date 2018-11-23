package com.nortal.treasurehunt.model;

public class TrailLog {
  private final Coordinates coordinates;
  private long timestamp;

  public TrailLog(Coordinates coordinates) {
    this.coordinates = coordinates;
    this.timestamp = System.currentTimeMillis();
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public long getTimeStamp() {
    return timestamp;
  }
}
