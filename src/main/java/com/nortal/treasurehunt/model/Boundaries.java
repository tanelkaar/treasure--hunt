package com.nortal.treasurehunt.model;

import com.nortal.treasurehunt.util.CoordinatesUtil;
import java.math.BigDecimal;

public class Boundaries {

  private final BigDecimal minLat;
  private final BigDecimal maxLat;
  private final BigDecimal minLng;
  private final BigDecimal maxLng;

  public Boundaries(Coordinates center, int marginMeters) {
    minLat = CoordinatesUtil.addLat(center, -marginMeters);
    maxLat = CoordinatesUtil.addLat(center, marginMeters);
    minLng = CoordinatesUtil.addLng(center, -marginMeters);
    maxLng = CoordinatesUtil.addLng(center, marginMeters);
  }

  public BigDecimal getMinLat() {
    return minLat;
  }

  public BigDecimal getMaxLat() {
    return maxLat;
  }

  public BigDecimal getMinLng() {
    return minLng;
  }

  public BigDecimal getMaxLng() {
    return maxLng;
  }
}
