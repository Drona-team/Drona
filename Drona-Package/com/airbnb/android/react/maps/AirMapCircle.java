package com.airbnb.android.react.maps;

import android.content.Context;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class AirMapCircle
  extends AirMapFeature
{
  private LatLng center;
  private Circle circle;
  private CircleOptions circleOptions;
  private int fillColor;
  private double radius;
  private int strokeColor;
  private float strokeWidth;
  private float zIndex;
  
  public AirMapCircle(Context paramContext)
  {
    super(paramContext);
  }
  
  private CircleOptions createCircleOptions()
  {
    CircleOptions localCircleOptions = new CircleOptions();
    localCircleOptions.center(center);
    localCircleOptions.radius(radius);
    localCircleOptions.fillColor(fillColor);
    localCircleOptions.strokeColor(strokeColor);
    localCircleOptions.strokeWidth(strokeWidth);
    localCircleOptions.zIndex(zIndex);
    return localCircleOptions;
  }
  
  public void addToMap(GoogleMap paramGoogleMap)
  {
    circle = paramGoogleMap.addCircle(getCircleOptions());
  }
  
  public CircleOptions getCircleOptions()
  {
    if (circleOptions == null) {
      circleOptions = createCircleOptions();
    }
    return circleOptions;
  }
  
  public Object getFeature()
  {
    return circle;
  }
  
  public void removeFromMap(GoogleMap paramGoogleMap)
  {
    circle.remove();
  }
  
  public void setCenter(LatLng paramLatLng)
  {
    center = paramLatLng;
    if (circle != null) {
      circle.setCenter(center);
    }
  }
  
  public void setFillColor(int paramInt)
  {
    fillColor = paramInt;
    if (circle != null) {
      circle.setFillColor(paramInt);
    }
  }
  
  public void setRadius(double paramDouble)
  {
    radius = paramDouble;
    if (circle != null) {
      circle.setRadius(radius);
    }
  }
  
  public void setStrokeColor(int paramInt)
  {
    strokeColor = paramInt;
    if (circle != null) {
      circle.setStrokeColor(paramInt);
    }
  }
  
  public void setStrokeWidth(float paramFloat)
  {
    strokeWidth = paramFloat;
    if (circle != null) {
      circle.setStrokeWidth(paramFloat);
    }
  }
  
  public void setZIndex(float paramFloat)
  {
    zIndex = paramFloat;
    if (circle != null) {
      circle.setZIndex(paramFloat);
    }
  }
}
