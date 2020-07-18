package com.airbnb.android.react.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class LatLngBoundsUtils
{
  public LatLngBoundsUtils() {}
  
  public static boolean BoundsAreDifferent(LatLngBounds paramLatLngBounds1, LatLngBounds paramLatLngBounds2)
  {
    LatLng localLatLng = paramLatLngBounds1.getCenter();
    double d1 = latitude;
    double d2 = longitude;
    double d3 = northeast.latitude;
    double d4 = southwest.latitude;
    double d5 = northeast.longitude;
    double d6 = southwest.longitude;
    localLatLng = paramLatLngBounds2.getCenter();
    double d7 = latitude;
    double d8 = longitude;
    double d9 = northeast.latitude;
    double d10 = southwest.latitude;
    double d11 = northeast.longitude;
    double d12 = southwest.longitude;
    double d13 = LatitudeEpsilon(paramLatLngBounds1, paramLatLngBounds2);
    double d14 = LongitudeEpsilon(paramLatLngBounds1, paramLatLngBounds2);
    return (different(d1, d7, d13)) || (different(d2, d8, d14)) || (different(d3 - d4, d9 - d10, d13)) || (different(d5 - d6, d11 - d12, d14));
  }
  
  private static double LatitudeEpsilon(LatLngBounds paramLatLngBounds1, LatLngBounds paramLatLngBounds2)
  {
    double d1 = northeast.latitude;
    double d2 = southwest.latitude;
    double d3 = northeast.latitude;
    double d4 = southwest.latitude;
    return Math.min(Math.abs(d1 - d2), Math.abs(d3 - d4)) / 2560.0D;
  }
  
  private static double LongitudeEpsilon(LatLngBounds paramLatLngBounds1, LatLngBounds paramLatLngBounds2)
  {
    double d1 = northeast.longitude;
    double d2 = southwest.longitude;
    double d3 = northeast.longitude;
    double d4 = southwest.longitude;
    return Math.min(Math.abs(d1 - d2), Math.abs(d3 - d4)) / 2560.0D;
  }
  
  private static boolean different(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    return Math.abs(paramDouble1 - paramDouble2) > paramDouble3;
  }
}
