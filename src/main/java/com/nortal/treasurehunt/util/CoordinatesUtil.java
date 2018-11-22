package com.nortal.treasurehunt.util;

import com.nortal.treasurehunt.model.Boundaries;
import com.nortal.treasurehunt.model.Coordinates;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CoordinatesUtil {
  public static final int RANGE = 20;

  // coordinate decimal accuracy is 7
  private static final int COORDINATES_ACCURACY_DECIMALS = 7;
  // 1 latitude degree is approximately 110.574 km
  private static final BigDecimal DEGREES_IN_LATITUDE_METER =
      new BigDecimal(1.0d / 110_574d).setScale(COORDINATES_ACCURACY_DECIMALS, RoundingMode.HALF_EVEN);
  // 1 longitude degree on equator is approximately 111.320 km
  private static final BigDecimal METERS_IN_LONGITUDE_DEGREE =
      new BigDecimal(111_320).setScale(COORDINATES_ACCURACY_DECIMALS);

  public static boolean intersects(Boundaries boundaries, Coordinates coordinates) {
    return boundaries.getMinLat().compareTo(coordinates.getLat()) < 0
        && boundaries.getMaxLat().compareTo(coordinates.getLat()) > 0
        && boundaries.getMinLng().compareTo(coordinates.getLng()) < 0
        && boundaries.getMaxLng().compareTo(coordinates.getLng()) > 0;
  }

  /**
   * Add meters to latitude degrees
   *
   * @param center
   * @param meters
   * @return Returns latitude degrees with meters added
   */
  public static BigDecimal addLat(Coordinates center, int meters) {
    return DEGREES_IN_LATITUDE_METER.multiply(BigDecimal.valueOf(meters)).add(center.getLat());
  }

  /**
   * Add meters to longitude degrees
   * 
   * @param center
   * @param meters
   * @return
   */
  public static BigDecimal addLng(Coordinates center, int meters) {
    // Longitude: 1 deg = 111.320*cos(latitude) km
    double latitudeInRadians = Math.toRadians(center.getLat().doubleValue());
    // because the earth is flat, you'll get less meters per degree when you get
    // further from the equator
    BigDecimal latitudeMultiplier = BigDecimal.valueOf(Math.cos(latitudeInRadians));
    return new BigDecimal(meters).divide(METERS_IN_LONGITUDE_DEGREE.multiply(latitudeMultiplier),
        COORDINATES_ACCURACY_DECIMALS,
        RoundingMode.HALF_EVEN).add(center.getLng());
  }

  public static Coordinates randomize(Coordinates coords) {
    return new Coordinates(getPos(coords.getLat()), getPos(coords.getLng()));
  }

  private static BigDecimal getPos(BigDecimal pos) {
    long multip = ((int) (Math.random() * 2)) % 2 == 0 ? 1 : -1;
    return pos.add(BigDecimal.valueOf(Math.random() / 500).multiply(BigDecimal.valueOf(multip)));
  }
}
