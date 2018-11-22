package com.nortal.treasurehunt.model;

import java.util.ArrayList;
import java.util.List;

public class ChallengeResponse {
  private String value;
  private List<Long> options = new ArrayList<>();

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
}
