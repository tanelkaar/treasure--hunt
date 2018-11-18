package com.nortal.treasurehunt.util;

import com.google.gson.Gson;
import com.nortal.treasurehunt.model.Game;

public class GameSerializationUtil {
  public static Game deserializeFromJSON(String jsonGameData) {
    return new Gson().fromJson(jsonGameData, Game.class);
  }

  public static String serializeToJSON(Game game) {
    return new Gson().toJson(game);
  }
}
