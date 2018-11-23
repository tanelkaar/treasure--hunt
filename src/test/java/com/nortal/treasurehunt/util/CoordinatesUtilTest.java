package com.nortal.treasurehunt.util;

import com.nortal.treasurehunt.model.Boundaries;
import com.nortal.treasurehunt.model.Coordinates;
import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.Test;

public class CoordinatesUtilTest {

  private static final Coordinates GREENWICH_SOUTH =
      new Coordinates(BigDecimal.valueOf(-77.988496046), BigDecimal.ZERO);
  private static final Coordinates GREENWICH_NORTH = new Coordinates(BigDecimal.valueOf(77.988496046), BigDecimal.ZERO);
  private static final Coordinates EQUATOR_EAST = new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(77.988496046));
  private static final Coordinates EQUATOR_WEST = new Coordinates(BigDecimal.ZERO, BigDecimal.valueOf(-77.988496046));

  @Test
  public void testIntersect() {
    int marginMeters = 100;

    // test somewhere on equator, in the west
    assertIntersect(marginMeters, EQUATOR_WEST);

    // Test on equator, in the east
    assertIntersect(marginMeters, EQUATOR_EAST);

    // Test on 0 meridian, in the north
    assertIntersect(marginMeters, GREENWICH_NORTH);

    // Test on 0 meridian, in the south
    assertIntersect(marginMeters, GREENWICH_SOUTH);
  }

  private void assertIntersect(int marginMeters, Coordinates center) {
    Boundaries boundaries = new Boundaries(center, marginMeters);
    System.out.println(boundaries);
    Assert.assertTrue(CoordinatesUtil.intersects(boundaries, center));
  }

  @Test
  public void testDistanceLng() {
    Assert.assertEquals(10,
        CoordinatesUtil.distance(EQUATOR_EAST,
            new Coordinates(EQUATOR_EAST.getLat(), CoordinatesUtil.addLng(EQUATOR_EAST, 10))));
  }

  @Test
  public void testDistanceLat() {
    Assert.assertEquals(10,
        CoordinatesUtil.distance(GREENWICH_NORTH,
            new Coordinates(CoordinatesUtil.addLat(GREENWICH_NORTH, 10), GREENWICH_NORTH.getLng())));
  }

  @Test
  public void testDistanceLatAndLng() {
    Assert.assertEquals(5,
        CoordinatesUtil.distance(GREENWICH_NORTH,
            new Coordinates(CoordinatesUtil.addLat(GREENWICH_NORTH, 3), CoordinatesUtil.addLng(GREENWICH_NORTH, 4))));
  }

}
