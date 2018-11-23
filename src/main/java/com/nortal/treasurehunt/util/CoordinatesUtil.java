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

  private static BigDecimal metersInLongitudeDegree;

  public static boolean intersects(Boundaries boundaries, Coordinates coordinates) {
    return boundaries.getMinLat().compareTo(coordinates.getLat()) < 0
        && boundaries.getMaxLat().compareTo(coordinates.getLat()) > 0
        && boundaries.getMinLng().compareTo(coordinates.getLng()) < 0
        && boundaries.getMaxLng().compareTo(coordinates.getLng()) > 0;
  }

  /**
   * Add meters to latitude degrees
   * @return Returns latitude degrees with meters added
   */
  public static BigDecimal addLat(Coordinates center, int meters) {
    return DEGREES_IN_LATITUDE_METER.multiply(BigDecimal.valueOf(meters)).add(center.getLat());
  }

  /**
   * Add meters to longitude degrees
   */
  public static BigDecimal addLng(Coordinates center, int meters) {
    return new BigDecimal(meters).divide(getMetersIngLongitudeDegree(center),
        COORDINATES_ACCURACY_DECIMALS,
        RoundingMode.HALF_EVEN).add(center.getLng());
  }

  private static BigDecimal getMetersIngLongitudeDegree(Coordinates center) {
    if(metersInLongitudeDegree == null) {
      // Longitude: 1 deg = 111.320*cos(latitude) km
      double latitudeInRadians = Math.toRadians(center.getLat().doubleValue());
      // because the earth is flat, you'll get less meters per degree when you get
      // further from the equator
      BigDecimal latitudeMultiplier = BigDecimal.valueOf(Math.cos(latitudeInRadians));
      metersInLongitudeDegree = METERS_IN_LONGITUDE_DEGREE.multiply(latitudeMultiplier);
    }
    return metersInLongitudeDegree;
  }

  public static Coordinates randomize(Coordinates coords) {
    return new Coordinates(getPos(coords.getLat()), getPos(coords.getLng()));
  }

  private static BigDecimal getPos(BigDecimal pos) {
    long multip = ((int) (Math.random() * 2)) % 2 == 0 ? 1 : -1;
    return pos.add(BigDecimal.valueOf(Math.random() / 500).multiply(BigDecimal.valueOf(multip)));
  }

  public static long distance(Coordinates a, Coordinates b) {
    BigDecimal latDeltaMeters = a.getLat().subtract(b.getLat()).abs().divide(DEGREES_IN_LATITUDE_METER, COORDINATES_ACCURACY_DECIMALS, RoundingMode.HALF_EVEN);
    BigDecimal lngDeltaMeters = a.getLng().subtract(b.getLng()).abs().multiply(getMetersIngLongitudeDegree(a));
    return Math.round(Math.sqrt(latDeltaMeters.pow(2).add(lngDeltaMeters.pow(2)).doubleValue()));
  }
}
