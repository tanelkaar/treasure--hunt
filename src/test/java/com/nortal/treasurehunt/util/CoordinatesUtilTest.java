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
    assertIntersect(marginMeters,
        new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(-77.988496046)));

    // Test on equator, in the east
    assertIntersect(marginMeters,
        new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(77.988496046)));

    // Test on 0 meridian, in the north
    assertIntersect(marginMeters,
        new Coordinates(BigDecimal.valueOf(77.988496046), BigDecimal.ZERO));

    // Test on 0 meridian, in the south
    assertIntersect(marginMeters,
        new Coordinates(BigDecimal.valueOf(-77.988496046), BigDecimal.ZERO));
  }

  private void assertIntersect(int marginMeters, Coordinates center) {
    Boundaries boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));
  }
}
