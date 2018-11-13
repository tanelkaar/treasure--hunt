package com.nortal.treasurehunt.util;

import com.nortal.treasurehunt.model.Boundaries;
import com.nortal.treasurehunt.model.Coordinates;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class CoordinatesUtilTest {

  @Test
  public void testIntersect() {
    int marginMeters = 100;

    // test somewhere on equator, in the west
    Coordinates center = new Coordinates(BigDecimal.ZERO,
        BigDecimal.valueOf(-77.988496046));
    Boundaries boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));

    // Test on equator, in the east
    center = new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(77.988496046));
    boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));

    // Test on 0 meridian, in the north
    center = new Coordinates(BigDecimal.valueOf(77.988496046), BigDecimal.ZERO);
    boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));

    // Test on 0 meridian, in the south
    center = new Coordinates(BigDecimal.valueOf(-77.988496046), BigDecimal.ZERO);
    boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));
  }
}
