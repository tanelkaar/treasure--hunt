package com.nortal.treasurehunt.util;

import com.nortal.treasurehunt.model.Boundaries;
import com.nortal.treasurehunt.model.Coordinates;
import java.math.BigDecimal;

public class CoordinatesUtil {

  // 1 latitude degree is approximately 110.574 km
  private static final int METERS_IN_LATITUDE_DEGREE = 110574;

  public static boolean intersects(Boundaries boundaries,
      Coordinates coordinates) {
    return boundaries.getMinLat().compareTo(coordinates.getLat()) < 0 &&
        boundaries.getMaxLat().compareTo(coordinates.getLat()) > 0 &&
        boundaries.getMinLng().compareTo(coordinates.getLng()) < 0 &&
        boundaries.getMaxLng().compareTo(coordinates.getLng()) > 0;
  }

  public static BigDecimal addLat(Coordinates center, int i) {
    // TODO Auto-generated method stub
    return null;
  }

  public static BigDecimal addLng(Coordinates center, int i) {
    // TODO Auto-generated method stub
    return null;
  }

}
