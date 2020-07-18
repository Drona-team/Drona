package com.airbnb.android.react.maps;

import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import java.util.Map;

public class AirMapPolygonManager
  extends ViewGroupManager<AirMapPolygon>
{
  private final DisplayMetrics metrics;
  
  public AirMapPolygonManager(ReactApplicationContext paramReactApplicationContext)
  {
    if (Build.VERSION.SDK_INT >= 17)
    {
      metrics = new DisplayMetrics();
      ((WindowManager)paramReactApplicationContext.getSystemService("window")).getDefaultDisplay().getRealMetrics(metrics);
      return;
    }
    metrics = paramReactApplicationContext.getResources().getDisplayMetrics();
  }
  
  public AirMapPolygon createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    return new AirMapPolygon(paramThemedReactContext);
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.get("onPress", MapBuilder.get("registrationName", "onPress"));
  }
  
  public String getName()
  {
    return "AIRMapPolygon";
  }
  
  public void setCoordinate(AirMapPolygon paramAirMapPolygon, ReadableArray paramReadableArray)
  {
    paramAirMapPolygon.setCoordinates(paramReadableArray);
  }
  
  public void setFillColor(AirMapPolygon paramAirMapPolygon, int paramInt)
  {
    paramAirMapPolygon.setFillColor(paramInt);
  }
  
  public void setGeodesic(AirMapPolygon paramAirMapPolygon, boolean paramBoolean)
  {
    paramAirMapPolygon.setGeodesic(paramBoolean);
  }
  
  public void setHoles(AirMapPolygon paramAirMapPolygon, ReadableArray paramReadableArray)
  {
    paramAirMapPolygon.setHoles(paramReadableArray);
  }
  
  public void setStrokeColor(AirMapPolygon paramAirMapPolygon, int paramInt)
  {
    paramAirMapPolygon.setStrokeColor(paramInt);
  }
  
  public void setStrokeWidth(AirMapPolygon paramAirMapPolygon, float paramFloat)
  {
    paramAirMapPolygon.setStrokeWidth(metrics.density * paramFloat);
  }
  
  public void setTappable(AirMapPolygon paramAirMapPolygon, boolean paramBoolean)
  {
    paramAirMapPolygon.setTappable(paramBoolean);
  }
  
  public void setZIndex(AirMapPolygon paramAirMapPolygon, float paramFloat)
  {
    paramAirMapPolygon.setZIndex(paramFloat);
  }
}
