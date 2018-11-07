package com.nortal.treasurehunt.model;

public class Challenge {

  private static final int CHALLENGE_BOUNDARIES_MARGIN_METERS = 10;

  private final Long id;
  private final String name;
  private final Coordinates coordinates;
  private final Boundaries boundaries;
  private final ChallengeType type;
  private final String text;

  public enum ChallengeType {
    TEXT,
    CHOICE,
    PICTURE;
  }

  public Challenge(Long id, String name, Coordinates coordinates, ChallengeType type, String text) {
    this.id = id;
    this.name = name;
    this.coordinates = coordinates;
    this.type = type;
    this.text = text;
    this.boundaries = new Boundaries(coordinates, CHALLENGE_BOUNDARIES_MARGIN_METERS);
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public ChallengeType getType() {
    return type;
  }

  public String getText() {
    return text;
  }

  public Boundaries getBoundaries() {
    return boundaries;
  }

}
