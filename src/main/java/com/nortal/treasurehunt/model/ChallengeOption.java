package com.nortal.treasurehunt.model;

public class ChallengeOption {
  private Long id;
  private String text;

  public ChallengeOption(Long id, String text) {
    this.id = id;
    this.text = text;
  }

  public Long getId() {
    return id;
  }

  public String getText() {
    return text;
  }
}
