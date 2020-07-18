package com.airbnb.android.react.maps;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.google.android.gms.maps.model.LatLng;

public class AirMapCircleManager
  extends ViewGroupManager<AirMapCircle>
{
  private final DisplayMetrics metrics;
  
  public AirMapCircleManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapCircle createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapCircle(paramThemedReactContext);
  }
  
  public String getName()
  {
    return "AIRMapCircle";
  }
  
  public void setCenter(AirMapCircle paramAirMapCircle, ReadableMap paramReadableMap)
  {
    paramAirMapCircle.setCenter(new LatLng(paramReadableMap.getDouble("latitude"), paramReadableMap.getDouble("longitude")));
  }
  
  public void setFillColor(AirMapCircle paramAirMapCircle, int paramInt)
  {
    paramAirMapCircle.setFillColor(paramInt);
  }
  
  public void setRadius(AirMapCircle paramAirMapCircle, double paramDouble)
  {
    paramAirMapCircle.setRadius(paramDouble);
  }
  
  public void setStrokeColor(AirMapCircle paramAirMapCircle, int paramInt)
  {
    paramAirMapCircle.setStrokeColor(paramInt);
  }
  
  public void setStrokeWidth(AirMapCircle paramAirMapCircle, float paramFloat)
  {
    paramAirMapCircle.setStrokeWidth(metrics.density * paramFloat);
  }
  
  public void setZIndex(AirMapCircle paramAirMapCircle, float paramFloat)
  {
    paramAirMapCircle.setZIndex(paramFloat);
  }
}
