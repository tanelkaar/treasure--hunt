package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.enums.ChallengeAnswerType;
import com.nortal.treasurehunt.enums.ChallengeType;
import java.util.ArrayList;
import java.util.List;

public class Challenge {
  private Long id;
  private Coordinates coordinates;
  private String text;
  private ChallengeType type;
  private ChallengeAnswerType answerType;
  private String image;
  private String video;
  private String url;
  private List<ChallengeOption> options = new ArrayList<>();

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Coordinates getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(Coordinates coordinates) {
    this.coordinates = coordinates;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public ChallengeType getType() {
    return type;
  }

  public void setType(ChallengeType type) {
    this.type = type;
  }

  public ChallengeAnswerType getAnswerType() {
    return answerType;
  }

  public void setAnswerType(ChallengeAnswerType answerType) {
    this.answerType = answerType;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getVideo() {
    return video;
  }

  public void setVideo(String video) {
    this.video = video;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public List<ChallengeOption> getOptions() {
    return options;
  }

  public void setOptions(List<ChallengeOption> options) {
    this.options = options;
  }

}
