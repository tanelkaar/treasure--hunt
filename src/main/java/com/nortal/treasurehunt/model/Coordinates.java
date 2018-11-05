package com.nortal.treasurehunt.model;

import java.math.BigDecimal;

/**
 * Latitude and longitude
 * @author Tanel Käär (Tanel.Kaar@nortal.com)
 */
public class Coordinates {

  private BigDecimal lat;
  private BigDecimal lng;

  public Coordinates(BigDecimal lat, BigDecimal lng) {
    this.lat = lat;
    this.lng = lng;
  }

  public BigDecimal getLat() {
    return lat;
  }

  public void setLat(BigDecimal lat) {
    this.lat = lat;
  }

  public BigDecimal getLng() {
    return lng;
  }

  public void setLng(BigDecimal lng) {
    this.lng = lng;
  }


}
