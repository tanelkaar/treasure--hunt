package com.nortal.treasurehunt.model;

import java.text.MessageFormat;

public class Message {
  private String text;
  private Object[] args;

  public Message(String text, Object... args) {
    this.text = text;
    this.args = args;
  }

  public String getText() {
    return MessageFormat.format(text, args);
  }
}
