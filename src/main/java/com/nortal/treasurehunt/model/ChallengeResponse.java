package com.nortal.treasurehunt.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ChallengeResponse {
  private String value;
  private List<Long> options = new ArrayList<>();
  private String image;
  private BigDecimal score;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<Long> getOptions() {
    return options;
  }

  public void setOptions(List<Long> options) {
    this.options = options;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getImage() {
    return image;
  }

  public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }
}
